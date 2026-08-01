package com.olympic.dakar.discipline;

import com.olympic.dakar.common.exception.ConflictException;
import com.olympic.dakar.common.exception.ResourceNotFoundException;
import com.olympic.dakar.discipline.dto.DisciplinePatchRequest;
import com.olympic.dakar.discipline.dto.DisciplineRequest;
import com.olympic.dakar.discipline.dto.DisciplineResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DisciplineServiceImplTest {

    @Mock
    private DisciplineRepository disciplineRepository;

    @InjectMocks
    private DisciplineServiceImpl disciplineService;

    private Discipline athletics;

    @BeforeEach
    void setUp() throws Exception {
        athletics = new Discipline("Athlétisme", "Courses et sauts");
        setId(athletics, 1L);
    }

    @Test
    void createShouldSaveWhenNameIsUnique() {
        DisciplineRequest request = new DisciplineRequest("Natation", "Bassin olympique");
        when(disciplineRepository.existsByNameIgnoreCase("Natation")).thenReturn(false);
        when(disciplineRepository.save(any(Discipline.class))).thenAnswer(invocation -> invocation.getArgument(0));

        DisciplineResponse response = disciplineService.create(request);

        assertThat(response.name()).isEqualTo("Natation");
        verify(disciplineRepository).save(any(Discipline.class));
    }

    @Test
    void createShouldThrowWhenNameAlreadyExists() {
        DisciplineRequest request = new DisciplineRequest("Athlétisme", "Doublon");
        when(disciplineRepository.existsByNameIgnoreCase("Athlétisme")).thenReturn(true);

        assertThatThrownBy(() -> disciplineService.create(request))
                .isInstanceOf(ConflictException.class);

        verify(disciplineRepository, never()).save(any());
    }

    @Test
    void findByIdShouldReturnMappedResponse() {
        when(disciplineRepository.findById(1L)).thenReturn(Optional.of(athletics));

        DisciplineResponse response = disciplineService.findById(1L);

        assertThat(response.id()).isEqualTo(1L);
        assertThat(response.name()).isEqualTo("Athlétisme");
    }

    @Test
    void findByIdShouldThrowWhenMissing() {
        when(disciplineRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> disciplineService.findById(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void updateShouldReplaceAllFields() {
        when(disciplineRepository.findById(1L)).thenReturn(Optional.of(athletics));
        when(disciplineRepository.findByNameIgnoreCase("Athletisme")).thenReturn(Optional.empty());
        DisciplineRequest request = new DisciplineRequest("Athletisme", "Nouvelle description");

        DisciplineResponse response = disciplineService.update(1L, request);

        assertThat(response.name()).isEqualTo("Athletisme");
        assertThat(response.description()).isEqualTo("Nouvelle description");
    }

    @Test
    void updateShouldThrowWhenRenamingToAnExistingName() throws Exception {
        Discipline swimming = new Discipline("Natation", null);
        setId(swimming, 2L);
        when(disciplineRepository.findById(1L)).thenReturn(Optional.of(athletics));
        when(disciplineRepository.findByNameIgnoreCase("Natation")).thenReturn(Optional.of(swimming));

        DisciplineRequest request = new DisciplineRequest("Natation", "Conflit");

        assertThatThrownBy(() -> disciplineService.update(1L, request))
                .isInstanceOf(ConflictException.class);
    }

    @Test
    void patchShouldOnlyUpdateProvidedFields() {
        when(disciplineRepository.findById(1L)).thenReturn(Optional.of(athletics));
        DisciplinePatchRequest patch = new DisciplinePatchRequest(null, "Description mise à jour");

        DisciplineResponse response = disciplineService.patch(1L, patch);

        assertThat(response.name()).isEqualTo("Athlétisme");
        assertThat(response.description()).isEqualTo("Description mise à jour");
    }

    @Test
    void patchShouldThrowWhenRenamingToAnExistingName() throws Exception {
        Discipline swimming = new Discipline("Natation", null);
        setId(swimming, 2L);
        when(disciplineRepository.findById(1L)).thenReturn(Optional.of(athletics));
        when(disciplineRepository.findByNameIgnoreCase("Natation")).thenReturn(Optional.of(swimming));

        DisciplinePatchRequest patch = new DisciplinePatchRequest("Natation", null);

        assertThatThrownBy(() -> disciplineService.patch(1L, patch))
                .isInstanceOf(ConflictException.class);
    }

    @Test
    void deleteShouldRemoveExistingDiscipline() {
        when(disciplineRepository.findById(1L)).thenReturn(Optional.of(athletics));

        disciplineService.delete(1L);

        verify(disciplineRepository).delete(athletics);
    }

    @Test
    void deleteShouldThrowWhenMissing() {
        when(disciplineRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> disciplineService.delete(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    private void setId(Object entity, Long id) throws Exception {
        Field field = entity.getClass().getDeclaredField("id");
        field.setAccessible(true);
        field.set(entity, id);
    }
}
