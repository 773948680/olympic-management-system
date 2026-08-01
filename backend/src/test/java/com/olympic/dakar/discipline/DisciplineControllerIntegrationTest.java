package com.olympic.dakar.discipline;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.olympic.dakar.athlete.Athlete;
import com.olympic.dakar.athlete.AthleteRepository;
import com.olympic.dakar.athlete.Gender;
import com.olympic.dakar.discipline.dto.DisciplineRequest;
import com.olympic.dakar.discipline.dto.DisciplineResponse;
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
class DisciplineControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private AthleteRepository athleteRepository;
    @Autowired
    private DisciplineRepository disciplineRepository;

    @Test
    void createShouldReturn201WithLocationAndBody() throws Exception {
        DisciplineRequest request = new DisciplineRequest("Escrime-IT", "Fleuret, épée, sabre");

        mockMvc.perform(post("/api/v1/disciplines")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("Escrime-IT"));
    }

    @Test
    void createShouldReturn400WhenNameIsBlank() throws Exception {
        String invalidJson = "{\"name\": \"\", \"description\": \"desc\"}";

        mockMvc.perform(post("/api/v1/disciplines")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.name").exists());
    }

    @Test
    void createShouldReturn409WhenNameAlreadyExists() throws Exception {
        DisciplineRequest request = new DisciplineRequest("Judo-IT", null);
        createDiscipline(request);

        mockMvc.perform(post("/api/v1/disciplines")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict());
    }

    @Test
    void findByIdShouldReturn200WhenExists() throws Exception {
        Long id = createDiscipline(new DisciplineRequest("Tir à l'arc-IT", null));

        mockMvc.perform(get("/api/v1/disciplines/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Tir à l'arc-IT"));
    }

    @Test
    void findByIdShouldReturn404WhenMissing() throws Exception {
        mockMvc.perform(get("/api/v1/disciplines/{id}", 999999))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void updateShouldReplaceAllFields() throws Exception {
        Long id = createDiscipline(new DisciplineRequest("Aviron-IT", "Ancienne description"));
        DisciplineRequest updateRequest = new DisciplineRequest("Aviron-IT-v2", "Nouvelle description");

        mockMvc.perform(put("/api/v1/disciplines/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Aviron-IT-v2"))
                .andExpect(jsonPath("$.description").value("Nouvelle description"));
    }

    @Test
    void patchShouldOnlyChangeProvidedFields() throws Exception {
        Long id = createDiscipline(new DisciplineRequest("Voile-IT", "Description initiale"));

        mockMvc.perform(patch("/api/v1/disciplines/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"description\": \"Description modifiée\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Voile-IT"))
                .andExpect(jsonPath("$.description").value("Description modifiée"));
    }

    @Test
    void deleteShouldRemoveDisciplineThenReturn404OnFetch() throws Exception {
        Long id = createDiscipline(new DisciplineRequest("Badminton-IT", null));

        mockMvc.perform(delete("/api/v1/disciplines/{id}", id))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/v1/disciplines/{id}", id))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteShouldReturn409WhenDisciplineHasAthletes() throws Exception {
        Discipline discipline = disciplineRepository.save(new Discipline("Haltérophilie-IT", null));
        athleteRepository.save(new Athlete("Test", "Athlete", Gender.MALE, LocalDate.of(1995, 1, 1),
                "Senegal", discipline, 180, 80.0));

        mockMvc.perform(delete("/api/v1/disciplines/{id}", discipline.getId()))
                .andExpect(status().isConflict());
    }

    @Test
    void findAthletesShouldReturnPaginatedAthletesOfDiscipline() throws Exception {
        Discipline discipline = disciplineRepository.save(new Discipline("Boxe-IT", null));
        athleteRepository.save(new Athlete("Mike", "Tyson", Gender.MALE, LocalDate.of(1966, 6, 30),
                "USA", discipline, 178, 100.0));

        mockMvc.perform(get("/api/v1/disciplines/{id}/athletes", discipline.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.content[0].lastName").value("Tyson"));
    }

    @Test
    void findAthletesShouldReturn404WhenDisciplineMissing() throws Exception {
        mockMvc.perform(get("/api/v1/disciplines/{id}/athletes", 999999))
                .andExpect(status().isNotFound());
    }

    private Long createDiscipline(DisciplineRequest request) throws Exception {
        MvcResult result = mockMvc.perform(post("/api/v1/disciplines")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn();
        DisciplineResponse response = objectMapper.readValue(result.getResponse().getContentAsString(), DisciplineResponse.class);
        return response.id();
    }
}
