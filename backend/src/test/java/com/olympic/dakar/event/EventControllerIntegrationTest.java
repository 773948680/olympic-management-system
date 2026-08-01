package com.olympic.dakar.event;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.olympic.dakar.athlete.Athlete;
import com.olympic.dakar.athlete.AthleteRepository;
import com.olympic.dakar.athlete.Gender;
import com.olympic.dakar.discipline.Discipline;
import com.olympic.dakar.discipline.DisciplineRepository;
import com.olympic.dakar.event.dto.EventRequest;
import com.olympic.dakar.event.dto.EventResponse;
import com.olympic.dakar.result.Result;
import com.olympic.dakar.result.ResultRepository;
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
import java.time.LocalDateTime;

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
class EventControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private DisciplineRepository disciplineRepository;
    @Autowired
    private AthleteRepository athleteRepository;
    @Autowired
    private ResultRepository resultRepository;
    @Autowired
    private EventRepository eventRepository;

    private Long athleticsId;
    private Long swimmingId;

    @BeforeEach
    void setUp() {
        athleticsId = disciplineRepository.save(new Discipline("Athlétisme-EV", null)).getId();
        swimmingId = disciplineRepository.save(new Discipline("Natation-EV", null)).getId();
    }

    private EventRequest sampleRequest(Long disciplineId, LocalDateTime eventDate) {
        return new EventRequest("100m", disciplineId, eventDate, "Stade Dakar", null);
    }

    @Test
    void createShouldReturn201WithLocationAndBody() throws Exception {
        mockMvc.perform(post("/api/v1/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleRequest(athleticsId, LocalDateTime.of(2026, 8, 10, 18, 0)))))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.status").value("SCHEDULED"))
                .andExpect(jsonPath("$.disciplineId").value(athleticsId));
    }

    @Test
    void createShouldReturn400WhenNameIsBlank() throws Exception {
        String invalidJson = """
                {"name": "", "disciplineId": %d, "eventDate": "2026-08-10T18:00:00"}
                """.formatted(athleticsId);

        mockMvc.perform(post("/api/v1/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.name").exists());
    }

    @Test
    void createShouldReturn404WhenDisciplineMissing() throws Exception {
        mockMvc.perform(post("/api/v1/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleRequest(999999L, LocalDateTime.now()))))
                .andExpect(status().isNotFound());
    }

    @Test
    void findByIdShouldReturn200WhenExists() throws Exception {
        Long id = createEvent(sampleRequest(athleticsId, LocalDateTime.of(2026, 8, 10, 18, 0)));

        mockMvc.perform(get("/api/v1/events/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("100m"));
    }

    @Test
    void findByIdShouldReturn404WhenMissing() throws Exception {
        mockMvc.perform(get("/api/v1/events/{id}", 999999))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void updateShouldReplaceAllFields() throws Exception {
        Long id = createEvent(sampleRequest(athleticsId, LocalDateTime.of(2026, 8, 10, 18, 0)));
        EventRequest updateRequest = new EventRequest("200m Nage Libre", swimmingId,
                LocalDateTime.of(2026, 8, 12, 10, 0), "Piscine Dakar", EventStatus.IN_PROGRESS);

        mockMvc.perform(put("/api/v1/events/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("200m Nage Libre"))
                .andExpect(jsonPath("$.disciplineId").value(swimmingId))
                .andExpect(jsonPath("$.status").value("IN_PROGRESS"));
    }

    @Test
    void patchShouldOnlyChangeProvidedFields() throws Exception {
        Long id = createEvent(sampleRequest(athleticsId, LocalDateTime.of(2026, 8, 10, 18, 0)));

        mockMvc.perform(patch("/api/v1/events/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"status\": \"COMPLETED\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("COMPLETED"))
                .andExpect(jsonPath("$.name").value("100m"))
                .andExpect(jsonPath("$.disciplineId").value(athleticsId));
    }

    @Test
    void deleteShouldRemoveEventThenReturn404OnFetch() throws Exception {
        Long id = createEvent(sampleRequest(athleticsId, LocalDateTime.of(2026, 8, 10, 18, 0)));

        mockMvc.perform(delete("/api/v1/events/{id}", id))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/v1/events/{id}", id))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteShouldReturn409WhenEventHasResults() throws Exception {
        Discipline discipline = disciplineRepository.findById(athleticsId).orElseThrow();
        Athlete athlete = athleteRepository.save(new Athlete("Usain", "Bolt", Gender.MALE,
                LocalDate.of(1986, 8, 21), "Jamaïque", discipline, 195, 94.0));
        Event event = eventRepository.save(new Event("100m", discipline,
                LocalDateTime.of(2026, 8, 10, 18, 0), null, EventStatus.SCHEDULED));
        resultRepository.save(new Result(event, athlete, 1, "9.58s", null));

        mockMvc.perform(delete("/api/v1/events/{id}", event.getId()))
                .andExpect(status().isConflict());
    }

    @Test
    void searchShouldCombineDisciplineAndDateFilters() throws Exception {
        createEvent(sampleRequest(athleticsId, LocalDateTime.of(2026, 8, 10, 18, 0)));
        createEvent(sampleRequest(athleticsId, LocalDateTime.of(2026, 8, 11, 9, 0)));
        createEvent(sampleRequest(swimmingId, LocalDateTime.of(2026, 8, 10, 9, 0)));

        mockMvc.perform(get("/api/v1/events").param("disciplineId", String.valueOf(athleticsId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(2));

        mockMvc.perform(get("/api/v1/events").param("date", "2026-08-10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(2));

        mockMvc.perform(get("/api/v1/events")
                        .param("disciplineId", String.valueOf(athleticsId))
                        .param("date", "2026-08-10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.content[0].disciplineId").value(athleticsId));
    }

    @Test
    void searchShouldSupportPaginationAndSort() throws Exception {
        for (int i = 0; i < 5; i++) {
            createEvent(new EventRequest("Event" + i, athleticsId,
                    LocalDateTime.of(2026, 8, 15, 10 + i, 0), null, null));
        }

        mockMvc.perform(get("/api/v1/events")
                        .param("disciplineId", String.valueOf(athleticsId))
                        .param("page", "0")
                        .param("size", "2")
                        .param("sort", "name,asc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.page").value(0))
                .andExpect(jsonPath("$.size").value(2))
                .andExpect(jsonPath("$.totalElements").value(5))
                .andExpect(jsonPath("$.totalPages").value(3))
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.content[0].name").value("Event0"))
                .andExpect(jsonPath("$.content[1].name").value("Event1"));
    }

    private Long createEvent(EventRequest request) throws Exception {
        MvcResult result = mockMvc.perform(post("/api/v1/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn();
        EventResponse response = objectMapper.readValue(result.getResponse().getContentAsString(), EventResponse.class);
        return response.id();
    }
}
