package com.olympic.dakar.result;

import com.olympic.dakar.athlete.Athlete;
import com.olympic.dakar.athlete.AthleteRepository;
import com.olympic.dakar.athlete.Gender;
import com.olympic.dakar.common.exception.BusinessRuleViolationException;
import com.olympic.dakar.common.exception.ConflictException;
import com.olympic.dakar.common.exception.ResourceNotFoundException;
import com.olympic.dakar.discipline.Discipline;
import com.olympic.dakar.event.Event;
import com.olympic.dakar.event.EventRepository;
import com.olympic.dakar.event.EventStatus;
import com.olympic.dakar.result.dto.ResultRequest;
import com.olympic.dakar.result.dto.ResultResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ResultServiceImplTest {

    @Mock
    private ResultRepository resultRepository;
    @Mock
    private EventRepository eventRepository;
    @Mock
    private AthleteRepository athleteRepository;

    @InjectMocks
    private ResultServiceImpl resultService;

    private Discipline athletics;
    private Discipline swimming;
    private Event event100m;
    private Athlete sprinter;

    @BeforeEach
    void setUp() throws Exception {
        athletics = newDisciplineWithId(1L, "Athlétisme");
        swimming = newDisciplineWithId(2L, "Natation");

        event100m = new Event("100m", athletics, LocalDateTime.now(), "Stade", EventStatus.SCHEDULED);
        setId(event100m, 10L);

        sprinter = new Athlete("Usain", "Bolt", Gender.MALE, LocalDate.of(1990, 1, 1),
                "Jamaïque", athletics, 195, 94.0);
        setId(sprinter, 100L);
    }

    @Test
    void createShouldAssignGoldForFirstPosition() {
        when(eventRepository.findById(10L)).thenReturn(Optional.of(event100m));
        when(athleteRepository.findById(100L)).thenReturn(Optional.of(sprinter));
        when(resultRepository.existsByEventIdAndAthleteId(10L, 100L)).thenReturn(false);
        when(resultRepository.existsByEventIdAndPosition(10L, 1)).thenReturn(false);
        when(resultRepository.save(any(Result.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ResultResponse response = resultService.create(new ResultRequest(10L, 100L, 1, "9.58s", null));

        assertThat(response.medal()).isEqualTo(MedalType.GOLD);
        assertThat(response.position()).isEqualTo(1);
        assertThat(response.time()).isEqualTo("9.58s");
    }

    @Test
    void createShouldAssignNoneWhenPositionIsBeyondPodium() {
        when(eventRepository.findById(10L)).thenReturn(Optional.of(event100m));
        when(athleteRepository.findById(100L)).thenReturn(Optional.of(sprinter));
        when(resultRepository.existsByEventIdAndAthleteId(10L, 100L)).thenReturn(false);
        when(resultRepository.existsByEventIdAndPosition(10L, 4)).thenReturn(false);
        when(resultRepository.save(any(Result.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ResultResponse response = resultService.create(new ResultRequest(10L, 100L, 4, "10.20s", null));

        assertThat(response.medal()).isEqualTo(MedalType.NONE);
    }

    @Test
    void createShouldSupportScoreBasedResults() {
        when(eventRepository.findById(10L)).thenReturn(Optional.of(event100m));
        when(athleteRepository.findById(100L)).thenReturn(Optional.of(sprinter));
        when(resultRepository.existsByEventIdAndAthleteId(10L, 100L)).thenReturn(false);
        when(resultRepository.existsByEventIdAndPosition(10L, 2)).thenReturn(false);
        when(resultRepository.save(any(Result.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ResultResponse response = resultService.create(new ResultRequest(10L, 100L, 2, null, 42.5));

        assertThat(response.medal()).isEqualTo(MedalType.SILVER);
        assertThat(response.score()).isEqualTo(42.5);
        assertThat(response.time()).isNull();
    }

    @Test
    void createShouldThrowWhenEventMissing() {
        when(eventRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> resultService.create(new ResultRequest(999L, 100L, 1, "9.58s", null)))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void createShouldThrowWhenAthleteMissing() {
        when(eventRepository.findById(10L)).thenReturn(Optional.of(event100m));
        when(athleteRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> resultService.create(new ResultRequest(10L, 999L, 1, "9.58s", null)))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void createShouldRejectAthleteFromDifferentDiscipline() throws Exception {
        Athlete swimmer = new Athlete("Michael", "Phelps", Gender.MALE, LocalDate.of(1985, 6, 30),
                "USA", swimming, 193, 88.0);
        setId(swimmer, 101L);

        when(eventRepository.findById(10L)).thenReturn(Optional.of(event100m));
        when(athleteRepository.findById(101L)).thenReturn(Optional.of(swimmer));

        assertThatThrownBy(() -> resultService.create(new ResultRequest(10L, 101L, 1, "48.0s", null)))
                .isInstanceOf(BusinessRuleViolationException.class);
    }

    @Test
    void createShouldRejectDuplicateAthleteForSameEvent() {
        when(eventRepository.findById(10L)).thenReturn(Optional.of(event100m));
        when(athleteRepository.findById(100L)).thenReturn(Optional.of(sprinter));
        when(resultRepository.existsByEventIdAndAthleteId(10L, 100L)).thenReturn(true);

        assertThatThrownBy(() -> resultService.create(new ResultRequest(10L, 100L, 2, "9.90s", null)))
                .isInstanceOf(ConflictException.class);
    }

    @Test
    void createShouldRejectDuplicatePositionForSameEvent() {
        when(eventRepository.findById(10L)).thenReturn(Optional.of(event100m));
        when(athleteRepository.findById(100L)).thenReturn(Optional.of(sprinter));
        when(resultRepository.existsByEventIdAndAthleteId(10L, 100L)).thenReturn(false);
        when(resultRepository.existsByEventIdAndPosition(10L, 1)).thenReturn(true);

        assertThatThrownBy(() -> resultService.create(new ResultRequest(10L, 100L, 1, "9.58s", null)))
                .isInstanceOf(ConflictException.class);
    }

    private Discipline newDisciplineWithId(Long id, String name) throws Exception {
        Discipline discipline = new Discipline(name, null);
        setId(discipline, id);
        return discipline;
    }

    private void setId(Object entity, Long id) throws Exception {
        Field field = entity.getClass().getDeclaredField("id");
        field.setAccessible(true);
        field.set(entity, id);
    }
}
