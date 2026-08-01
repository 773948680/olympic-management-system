package com.olympic.dakar.dashboard;

import com.olympic.dakar.athlete.Athlete;
import com.olympic.dakar.athlete.AthleteRepository;
import com.olympic.dakar.athlete.Gender;
import com.olympic.dakar.discipline.Discipline;
import com.olympic.dakar.discipline.DisciplineRepository;
import com.olympic.dakar.event.Event;
import com.olympic.dakar.event.EventRepository;
import com.olympic.dakar.event.EventStatus;
import com.olympic.dakar.result.Result;
import com.olympic.dakar.result.ResultRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser
@Transactional
class DashboardControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private DisciplineRepository disciplineRepository;
    @Autowired
    private AthleteRepository athleteRepository;
    @Autowired
    private EventRepository eventRepository;
    @Autowired
    private ResultRepository resultRepository;

    @Test
    void athletesCountShouldReflectNumberOfAthletes() throws Exception {
        Discipline discipline = disciplineRepository.save(new Discipline("Athlétisme-DB1", null));
        athleteRepository.save(newAthlete("Usain", "Bolt", "Jamaïque", discipline));
        athleteRepository.save(newAthlete("Justin", "Gatlin", "USA", discipline));
        athleteRepository.save(newAthlete("Michael", "Phelps", "USA", discipline));

        mockMvc.perform(get("/api/v1/dashboard/athletes/count"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.count").value(3));
    }

    @Test
    void countriesCountShouldReflectDistinctNationalities() throws Exception {
        Discipline discipline = disciplineRepository.save(new Discipline("Athlétisme-DB2", null));
        athleteRepository.save(newAthlete("Usain", "Bolt", "Jamaïque", discipline));
        athleteRepository.save(newAthlete("Justin", "Gatlin", "USA", discipline));
        athleteRepository.save(newAthlete("Michael", "Phelps", "USA", discipline));

        mockMvc.perform(get("/api/v1/dashboard/countries/count"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.count").value(2));
    }

    @Test
    void medalsShouldReturnGlobalTotalsAcrossAllCountries() throws Exception {
        Discipline discipline = disciplineRepository.save(new Discipline("Athlétisme-DB3", null));
        Event event1 = eventRepository.save(newEvent("100m", discipline));
        Event event2 = eventRepository.save(newEvent("200m", discipline));

        Athlete bolt = athleteRepository.save(newAthlete("Usain", "Bolt", "Jamaïque", discipline));
        Athlete gatlin = athleteRepository.save(newAthlete("Justin", "Gatlin", "USA", discipline));
        Athlete blake = athleteRepository.save(newAthlete("Yohan", "Blake", "Jamaïque", discipline));

        resultRepository.save(new Result(event1, bolt, 1, "9.58s", null));
        resultRepository.save(new Result(event1, gatlin, 2, "9.75s", null));
        resultRepository.save(new Result(event2, blake, 1, "19.4s", null));
        resultRepository.save(new Result(event2, gatlin, 4, "20.5s", null));

        mockMvc.perform(get("/api/v1/dashboard/medals"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.gold").value(2))
                .andExpect(jsonPath("$.silver").value(1))
                .andExpect(jsonPath("$.bronze").value(0))
                .andExpect(jsonPath("$.total").value(3));
    }

    @Test
    void countriesRankingShouldSortByPointsNotByGoldCount() throws Exception {
        Discipline discipline = disciplineRepository.save(new Discipline("Athlétisme-DB4", null));
        Event event = eventRepository.save(newEvent("100m", discipline));

        Athlete goldWinner = athleteRepository.save(newAthlete("Gold", "Winner", "NationA", discipline));
        Athlete silver1 = athleteRepository.save(newAthlete("Silver", "One", "NationB", discipline));
        Event event2 = eventRepository.save(newEvent("200m", discipline));
        Athlete silver2 = athleteRepository.save(newAthlete("Silver", "Two", "NationB", discipline));

        resultRepository.save(new Result(event, goldWinner, 1, "9.58s", null));
        resultRepository.save(new Result(event, silver1, 2, "9.75s", null));
        resultRepository.save(new Result(event2, silver2, 2, "19.9s", null));

        mockMvc.perform(get("/api/v1/dashboard/countries/ranking"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nationality").value("NationB"))
                .andExpect(jsonPath("$[0].points").value(8))
                .andExpect(jsonPath("$[1].nationality").value("NationA"))
                .andExpect(jsonPath("$[1].points").value(7));
    }

    @Test
    void countriesMedalistsShouldCountDistinctAthletesNotDistinctMedals() throws Exception {
        Discipline discipline = disciplineRepository.save(new Discipline("Athlétisme-DB5", null));
        Event event1 = eventRepository.save(newEvent("100m", discipline));
        Event event2 = eventRepository.save(newEvent("200m", discipline));

        Athlete doubleMedalist = athleteRepository.save(newAthlete("Double", "Medalist", "NationC", discipline));
        Athlete singleMedalist = athleteRepository.save(newAthlete("Single", "Medalist", "NationD", discipline));

        resultRepository.save(new Result(event1, doubleMedalist, 1, "9.58s", null));
        resultRepository.save(new Result(event2, doubleMedalist, 2, "19.9s", null));
        resultRepository.save(new Result(event1, singleMedalist, 3, "9.99s", null));

        mockMvc.perform(get("/api/v1/dashboard/countries/medalists"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nationality").value("NationC"))
                .andExpect(jsonPath("$[0].medalists").value(1))
                .andExpect(jsonPath("$[1].nationality").value("NationD"))
                .andExpect(jsonPath("$[1].medalists").value(1));
    }

    private Athlete newAthlete(String firstName, String lastName, String nationality, Discipline discipline) {
        return new Athlete(firstName, lastName, Gender.MALE, LocalDate.of(1990, 1, 1),
                nationality, discipline, 180, 75.0);
    }

    private Event newEvent(String name, Discipline discipline) {
        return new Event(name, discipline, LocalDateTime.of(2026, 8, 10, 18, 0), null, EventStatus.SCHEDULED);
    }
}
