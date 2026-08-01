package com.olympic.dakar.result;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.olympic.dakar.athlete.Athlete;
import com.olympic.dakar.athlete.AthleteRepository;
import com.olympic.dakar.athlete.Gender;
import com.olympic.dakar.discipline.Discipline;
import com.olympic.dakar.discipline.DisciplineRepository;
import com.olympic.dakar.event.Event;
import com.olympic.dakar.event.EventRepository;
import com.olympic.dakar.event.EventStatus;
import com.olympic.dakar.result.dto.ResultRequest;
import com.olympic.dakar.result.dto.ResultResponse;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser
@Transactional
class ResultControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private DisciplineRepository disciplineRepository;
    @Autowired
    private AthleteRepository athleteRepository;
    @Autowired
    private EventRepository eventRepository;

    private Long eventId;
    private Long athleteId;
    private Long secondAthleteId;

    @BeforeEach
    void setUp() {
        Discipline discipline = disciplineRepository.save(new Discipline("Athlétisme-RS", null));
        Event event = eventRepository.save(new Event("100m", discipline,
                LocalDateTime.of(2026, 8, 10, 18, 0), "Stade Dakar", EventStatus.SCHEDULED));
        eventId = event.getId();

        Athlete athlete = athleteRepository.save(new Athlete("Usain", "Bolt", Gender.MALE,
                LocalDate.of(1986, 8, 21), "Jamaïque", discipline, 195, 94.0));
        athleteId = athlete.getId();

        Athlete second = athleteRepository.save(new Athlete("Justin", "Gatlin", Gender.MALE,
                LocalDate.of(1982, 2, 10), "USA", discipline, 185, 82.0));
        secondAthleteId = second.getId();
    }

    @Test
    void createShouldReturn201AndAssignGoldForFirstPosition() throws Exception {
        ResultRequest request = new ResultRequest(eventId, athleteId, 1, "9.58s", null);

        mockMvc.perform(post("/api/v1/results")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.medal").value("GOLD"))
                .andExpect(jsonPath("$.position").value(1))
                .andExpect(jsonPath("$.time").value("9.58s"));
    }

    @Test
    void createShouldReturn400WhenPositionMissing() throws Exception {
        String invalidJson = """
                {"eventId": %d, "athleteId": %d, "time": "9.58s"}
                """.formatted(eventId, athleteId);

        mockMvc.perform(post("/api/v1/results")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.position").exists());
    }

    @Test
    void createShouldReturn404WhenEventMissing() throws Exception {
        ResultRequest request = new ResultRequest(999999L, athleteId, 1, "9.58s", null);

        mockMvc.perform(post("/api/v1/results")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    void createShouldReturn404WhenAthleteMissing() throws Exception {
        ResultRequest request = new ResultRequest(eventId, 999999L, 1, "9.58s", null);

        mockMvc.perform(post("/api/v1/results")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    void createShouldReturn409WhenAthleteAlreadyHasResultForEvent() throws Exception {
        createResult(new ResultRequest(eventId, athleteId, 1, "9.58s", null));

        mockMvc.perform(post("/api/v1/results")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new ResultRequest(eventId, athleteId, 2, "9.90s", null))))
                .andExpect(status().isConflict());
    }

    @Test
    void createShouldReturn409WhenPositionAlreadyTaken() throws Exception {
        createResult(new ResultRequest(eventId, athleteId, 1, "9.58s", null));

        mockMvc.perform(post("/api/v1/results")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new ResultRequest(eventId, secondAthleteId, 1, "9.60s", null))))
                .andExpect(status().isConflict());
    }

    @Test
    void findByIdShouldReturn200WhenExists() throws Exception {
        Long id = createResult(new ResultRequest(eventId, athleteId, 1, "9.58s", null));

        mockMvc.perform(get("/api/v1/results/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.medal").value("GOLD"));
    }

    @Test
    void findByIdShouldReturn404WhenMissing() throws Exception {
        mockMvc.perform(get("/api/v1/results/{id}", 999999))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateShouldRecomputeMedalWhenPositionChanges() throws Exception {
        Long id = createResult(new ResultRequest(eventId, athleteId, 1, "9.58s", null));
        ResultRequest update = new ResultRequest(eventId, athleteId, 3, "9.99s", null);

        mockMvc.perform(put("/api/v1/results/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(update)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.medal").value("BRONZE"))
                .andExpect(jsonPath("$.position").value(3));
    }

    @Test
    void deleteShouldRemoveResultThenReturn404OnFetch() throws Exception {
        Long id = createResult(new ResultRequest(eventId, athleteId, 1, "9.58s", null));

        mockMvc.perform(delete("/api/v1/results/{id}", id))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/v1/results/{id}", id))
                .andExpect(status().isNotFound());
    }

    @Test
    void findByEventShouldReturnResultsOrderedByPosition() throws Exception {
        createResult(new ResultRequest(eventId, secondAthleteId, 2, "9.90s", null));
        createResult(new ResultRequest(eventId, athleteId, 1, "9.58s", null));

        mockMvc.perform(get("/api/v1/events/{eventId}/results", eventId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].position").value(1))
                .andExpect(jsonPath("$[1].position").value(2));
    }

    @Test
    void podiumShouldOnlyContainMedalists() throws Exception {
        createResult(new ResultRequest(eventId, athleteId, 1, "9.58s", null));
        createResult(new ResultRequest(eventId, secondAthleteId, 4, "10.20s", null));

        mockMvc.perform(get("/api/v1/events/{eventId}/podium", eventId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].medal").value("GOLD"));
    }

    @Test
    void medalTableShouldAggregateAndSortByGoldSilverBronze() throws Exception {
        createResult(new ResultRequest(eventId, athleteId, 1, "9.58s", null));
        createResult(new ResultRequest(eventId, secondAthleteId, 2, "9.90s", null));

        mockMvc.perform(get("/api/v1/medals/medal-table"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nationality").value("Jamaïque"))
                .andExpect(jsonPath("$[0].gold").value(1))
                .andExpect(jsonPath("$[1].nationality").value("USA"))
                .andExpect(jsonPath("$[1].silver").value(1));
    }

    private Long createResult(ResultRequest request) throws Exception {
        MvcResult result = mockMvc.perform(post("/api/v1/results")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn();
        ResultResponse response = objectMapper.readValue(result.getResponse().getContentAsString(), ResultResponse.class);
        return response.id();
    }
}
