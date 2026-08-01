package com.olympic.dakar.athlete;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.olympic.dakar.athlete.dto.AthleteRequest;
import com.olympic.dakar.athlete.dto.AthleteResponse;
import com.olympic.dakar.discipline.Discipline;
import com.olympic.dakar.discipline.DisciplineRepository;
import jakarta.persistence.EntityManagerFactory;
import org.hibernate.SessionFactory;
import org.hibernate.stat.Statistics;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser
@Transactional
class AthleteControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private DisciplineRepository disciplineRepository;
    @Autowired
    private EntityManagerFactory entityManagerFactory;

    private Long athleticsId;
    private Long swimmingId;

    @BeforeEach
    void setUp() {
        athleticsId = disciplineRepository.save(new Discipline("Athlétisme-IT", null)).getId();
        swimmingId = disciplineRepository.save(new Discipline("Natation-IT", null)).getId();
    }

    @Test
    void createShouldReturn201WithLocationAndBody() throws Exception {
        mockMvc.perform(post("/api/v1/athletes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleRequest(athleticsId))))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.firstName").value("Usain"))
                .andExpect(jsonPath("$.disciplineId").value(athleticsId));
    }

    @Test
    void createShouldReturn400WhenValidationFails() throws Exception {
        String invalidJson = """
                {"firstName": "", "lastName": "Bolt", "gender": "MALE",
                 "dateOfBirth": "1986-08-21", "nationality": "Jamaïque",
                 "disciplineId": %d, "height": 195, "weight": 94.0}
                """.formatted(athleticsId);

        mockMvc.perform(post("/api/v1/athletes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.firstName").exists());
    }

    @Test
    void createShouldReturn404WhenDisciplineMissing() throws Exception {
        mockMvc.perform(post("/api/v1/athletes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleRequest(999999L))))
                .andExpect(status().isNotFound());
    }

    @Test
    void findByIdShouldReturn200WhenExists() throws Exception {
        Long id = createAthlete(sampleRequest(athleticsId));

        mockMvc.perform(get("/api/v1/athletes/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.lastName").value("Bolt"));
    }

    @Test
    void findByIdShouldReturn404WhenMissing() throws Exception {
        mockMvc.perform(get("/api/v1/athletes/{id}", 999999))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void findByIdShouldReturn400WhenIdIsNotNumeric() throws Exception {
        mockMvc.perform(get("/api/v1/athletes/{id}", "abc"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void createShouldReturn400WhenBodyIsMalformedJson() throws Exception {
        mockMvc.perform(post("/api/v1/athletes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{not-valid-json"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void searchShouldReturn400WhenGenderParamIsInvalid() throws Exception {
        mockMvc.perform(get("/api/v1/athletes").param("gender", "OTHER"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void updateShouldReplaceAllFields() throws Exception {
        Long id = createAthlete(sampleRequest(athleticsId));

        AthleteRequest updateRequest = new AthleteRequest("Usain", "Bolt", Gender.MALE,
                LocalDate.of(1986, 8, 21), "Jamaica", swimmingId, 196, 95.0);

        mockMvc.perform(put("/api/v1/athletes/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nationality").value("Jamaica"))
                .andExpect(jsonPath("$.disciplineId").value(swimmingId))
                .andExpect(jsonPath("$.height").value(196));
    }

    @Test
    void updateShouldReturn400WhenValidationFails() throws Exception {
        Long id = createAthlete(sampleRequest(athleticsId));
        String invalidJson = """
                {"firstName": "Usain", "lastName": "Bolt", "gender": "MALE",
                 "dateOfBirth": "1986-08-21", "nationality": "Jamaïque",
                 "disciplineId": %d, "height": 400, "weight": 94.0}
                """.formatted(athleticsId);

        mockMvc.perform(put("/api/v1/athletes/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.height").exists());
    }

    @Test
    void patchShouldOnlyChangeProvidedFields() throws Exception {
        Long id = createAthlete(sampleRequest(athleticsId));

        mockMvc.perform(patch("/api/v1/athletes/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nationality\": \"Jamaica\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nationality").value("Jamaica"))
                .andExpect(jsonPath("$.firstName").value("Usain"))
                .andExpect(jsonPath("$.lastName").value("Bolt"));
    }

    @Test
    void deleteShouldRemoveAthleteThenReturn404OnFetch() throws Exception {
        Long id = createAthlete(sampleRequest(athleticsId));

        mockMvc.perform(delete("/api/v1/athletes/{id}", id))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/v1/athletes/{id}", id))
                .andExpect(status().isNotFound());
    }

    @Test
    void searchShouldFilterByMulticriteria() throws Exception {
        createAthlete(new AthleteRequest("Usain", "Bolt", Gender.MALE, LocalDate.of(1986, 8, 21),
                "Jamaïque", athleticsId, 195, 94.0));
        createAthlete(new AthleteRequest("Michael", "Phelps", Gender.MALE, LocalDate.of(1985, 6, 30),
                "USA", swimmingId, 193, 88.0));
        createAthlete(new AthleteRequest("Shelly-Ann", "Fraser", Gender.FEMALE, LocalDate.of(1986, 12, 27),
                "Jamaïque", athleticsId, 152, 52.0));

        mockMvc.perform(get("/api/v1/athletes")
                        .param("nationality", "Jamaïque")
                        .param("gender", "FEMALE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.content[0].lastName").value("Fraser"));

        mockMvc.perform(get("/api/v1/athletes")
                        .param("disciplineId", String.valueOf(athleticsId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(2));

        mockMvc.perform(get("/api/v1/athletes")
                        .param("lastName", "bolt"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.content[0].firstName").value("Usain"));
    }

    @Test
    void searchShouldSupportPaginationAndSort() throws Exception {
        for (int i = 0; i < 5; i++) {
            createAthlete(new AthleteRequest("First" + i, "Last" + i, Gender.MALE,
                    LocalDate.of(1990, 1, 1), "Senegal", athleticsId, 180, 75.0));
        }

        mockMvc.perform(get("/api/v1/athletes")
                        .param("nationality", "Senegal")
                        .param("page", "0")
                        .param("size", "2")
                        .param("sort", "lastName,asc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.page").value(0))
                .andExpect(jsonPath("$.size").value(2))
                .andExpect(jsonPath("$.totalElements").value(5))
                .andExpect(jsonPath("$.totalPages").value(3))
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.content[0].lastName").value("Last0"))
                .andExpect(jsonPath("$.content[1].lastName").value("Last1"));
    }

    private AthleteRequest sampleRequest(Long disciplineId) {
        return new AthleteRequest("Usain", "Bolt", Gender.MALE, LocalDate.of(1986, 8, 21),
                "Jamaïque", disciplineId, 195, 94.0);
    }

    @Test
    void searchShouldNotTriggerNPlusOneQueriesForDiscipline() throws Exception {
        for (int i = 0; i < 5; i++) {
            Long disciplineId = disciplineRepository.save(new Discipline("Discipline-NPlusOne-" + i, null)).getId();
            createAthlete(new AthleteRequest("First" + i, "Last" + i, Gender.MALE,
                    LocalDate.of(1990, 1, 1), "Senegal-NPlusOne", disciplineId, 180, 75.0));
        }

        Statistics statistics = entityManagerFactory.unwrap(SessionFactory.class).getStatistics();
        statistics.clear();

        mockMvc.perform(get("/api/v1/athletes").param("nationality", "Senegal-NPlusOne"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(5));

        long queryCount = statistics.getPrepareStatementCount();
        assertThat(queryCount)
                .as("le listing ne doit pas exécuter une requête de discipline par athlète (N+1)")
                .isLessThanOrEqualTo(3);
    }

    private Long createAthlete(AthleteRequest request) throws Exception {
        MvcResult result = mockMvc.perform(post("/api/v1/athletes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn();
        AthleteResponse response = objectMapper.readValue(result.getResponse().getContentAsString(), AthleteResponse.class);
        return response.id();
    }
}
