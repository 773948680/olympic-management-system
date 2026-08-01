package com.olympic.dakar.athlete;

import com.olympic.dakar.athlete.dto.AthletePatchRequest;
import com.olympic.dakar.athlete.dto.AthleteRequest;
import com.olympic.dakar.athlete.dto.AthleteResponse;
import com.olympic.dakar.common.exception.ResourceNotFoundException;
import com.olympic.dakar.discipline.Discipline;
import com.olympic.dakar.discipline.DisciplineRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AthleteServiceImplTest {

    @Mock
    private AthleteRepository athleteRepository;
    @Mock
    private DisciplineRepository disciplineRepository;

    @InjectMocks
    private AthleteServiceImpl athleteService;

    private Discipline athletics;
    private Athlete athlete;

    @BeforeEach
    void setUp() throws Exception {
        athletics = new Discipline("Athlétisme", null);
        setId(athletics, 1L);
        athlete = new Athlete("Usain", "Bolt", Gender.MALE, LocalDate.of(1986, 8, 21),
                "Jamaïque", athletics, 195, 94.0);
        setId(athlete, 100L);
    }

    @Test
    void createShouldSaveAthleteWithResolvedDiscipline() {
        AthleteRequest request = new AthleteRequest("Usain", "Bolt", Gender.MALE,
                LocalDate.of(1986, 8, 21), "Jamaïque", 1L, 195, 94.0);
        when(disciplineRepository.findById(1L)).thenReturn(Optional.of(athletics));
        when(athleteRepository.save(any(Athlete.class))).thenAnswer(invocation -> invocation.getArgument(0));

        AthleteResponse response = athleteService.create(request);

        assertThat(response.firstName()).isEqualTo("Usain");
        assertThat(response.disciplineId()).isEqualTo(1L);
        verify(athleteRepository).save(any(Athlete.class));
    }

    @Test
    void createShouldThrowWhenDisciplineMissing() {
        AthleteRequest request = new AthleteRequest("Usain", "Bolt", Gender.MALE,
                LocalDate.of(1986, 8, 21), "Jamaïque", 99L, 195, 94.0);
        when(disciplineRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> athleteService.create(request))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void findByIdShouldReturnMappedResponse() {
        when(athleteRepository.findById(100L)).thenReturn(Optional.of(athlete));

        AthleteResponse response = athleteService.findById(100L);

        assertThat(response.id()).isEqualTo(100L);
        assertThat(response.lastName()).isEqualTo("Bolt");
    }

    @Test
    void findByIdShouldThrowWhenMissing() {
        when(athleteRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> athleteService.findById(999L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void updateShouldReplaceAllFields() {
        when(athleteRepository.findById(100L)).thenReturn(Optional.of(athlete));
        when(disciplineRepository.findById(1L)).thenReturn(Optional.of(athletics));
        AthleteRequest request = new AthleteRequest("Usain", "Bolt", Gender.MALE,
                LocalDate.of(1986, 8, 21), "Jamaica", 1L, 196, 95.0);

        AthleteResponse response = athleteService.update(100L, request);

        assertThat(response.nationality()).isEqualTo("Jamaica");
        assertThat(response.height()).isEqualTo(196);
        assertThat(response.weight()).isEqualTo(95.0);
    }

    @Test
    void patchShouldOnlyUpdateProvidedFields() {
        when(athleteRepository.findById(100L)).thenReturn(Optional.of(athlete));
        AthletePatchRequest patch = new AthletePatchRequest(null, null, null, null,
                "Jamaica", null, null, null);

        AthleteResponse response = athleteService.patch(100L, patch);

        assertThat(response.nationality()).isEqualTo("Jamaica");
        assertThat(response.firstName()).isEqualTo("Usain");
        assertThat(response.lastName()).isEqualTo("Bolt");
        assertThat(response.height()).isEqualTo(195);
    }

    @Test
    void deleteShouldRemoveExistingAthlete() {
        when(athleteRepository.findById(100L)).thenReturn(Optional.of(athlete));

        athleteService.delete(100L);

        verify(athleteRepository).delete(athlete);
    }

    @Test
    void deleteShouldThrowWhenMissing() {
        when(athleteRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> athleteService.delete(999L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    private void setId(Object entity, Long id) throws Exception {
        Field field = entity.getClass().getDeclaredField("id");
        field.setAccessible(true);
        field.set(entity, id);
    }
}
