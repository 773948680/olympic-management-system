package com.olympic.dakar.event;

import com.olympic.dakar.common.exception.ResourceNotFoundException;
import com.olympic.dakar.discipline.Discipline;
import com.olympic.dakar.discipline.DisciplineRepository;
import com.olympic.dakar.event.dto.EventPatchRequest;
import com.olympic.dakar.event.dto.EventRequest;
import com.olympic.dakar.event.dto.EventResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EventServiceImplTest {

    @Mock
    private EventRepository eventRepository;
    @Mock
    private DisciplineRepository disciplineRepository;

    @InjectMocks
    private EventServiceImpl eventService;

    private Discipline athletics;
    private Discipline swimming;
    private Event event;

    @BeforeEach
    void setUp() throws Exception {
        athletics = new Discipline("Athlétisme", null);
        setId(athletics, 1L);
        swimming = new Discipline("Natation", null);
        setId(swimming, 2L);
        event = new Event("100m", athletics, LocalDateTime.of(2026, 8, 10, 18, 0), "Stade Dakar", EventStatus.SCHEDULED);
        setId(event, 10L);
    }

    @Test
    void createShouldSaveEventWithResolvedDiscipline() {
        EventRequest request = new EventRequest("100m", 1L, LocalDateTime.of(2026, 8, 10, 18, 0), "Stade Dakar", null);
        when(disciplineRepository.findById(1L)).thenReturn(Optional.of(athletics));
        when(eventRepository.save(any(Event.class))).thenAnswer(invocation -> invocation.getArgument(0));

        EventResponse response = eventService.create(request);

        assertThat(response.name()).isEqualTo("100m");
        assertThat(response.disciplineId()).isEqualTo(1L);
        assertThat(response.status()).isEqualTo(EventStatus.SCHEDULED);
        verify(eventRepository).save(any(Event.class));
    }

    @Test
    void createShouldThrowWhenDisciplineMissing() {
        EventRequest request = new EventRequest("100m", 99L, LocalDateTime.now(), null, null);
        when(disciplineRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> eventService.create(request))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void findByIdShouldReturnMappedResponse() {
        when(eventRepository.findById(10L)).thenReturn(Optional.of(event));

        EventResponse response = eventService.findById(10L);

        assertThat(response.id()).isEqualTo(10L);
        assertThat(response.name()).isEqualTo("100m");
    }

    @Test
    void findByIdShouldThrowWhenMissing() {
        when(eventRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> eventService.findById(999L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void updateShouldReplaceAllFields() {
        when(eventRepository.findById(10L)).thenReturn(Optional.of(event));
        when(disciplineRepository.findById(2L)).thenReturn(Optional.of(swimming));
        EventRequest request = new EventRequest("200m Nage Libre", 2L,
                LocalDateTime.of(2026, 8, 12, 10, 0), "Piscine Dakar", EventStatus.IN_PROGRESS);

        EventResponse response = eventService.update(10L, request);

        assertThat(response.name()).isEqualTo("200m Nage Libre");
        assertThat(response.disciplineId()).isEqualTo(2L);
        assertThat(response.status()).isEqualTo(EventStatus.IN_PROGRESS);
    }

    @Test
    void patchShouldOnlyUpdateProvidedFields() {
        when(eventRepository.findById(10L)).thenReturn(Optional.of(event));
        EventPatchRequest patch = new EventPatchRequest(null, null, null, null, EventStatus.COMPLETED);

        EventResponse response = eventService.patch(10L, patch);

        assertThat(response.status()).isEqualTo(EventStatus.COMPLETED);
        assertThat(response.name()).isEqualTo("100m");
        assertThat(response.disciplineId()).isEqualTo(1L);
    }

    @Test
    void deleteShouldRemoveExistingEvent() {
        when(eventRepository.findById(10L)).thenReturn(Optional.of(event));

        eventService.delete(10L);

        verify(eventRepository).delete(event);
    }

    @Test
    void deleteShouldThrowWhenMissing() {
        when(eventRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> eventService.delete(999L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    private void setId(Object entity, Long id) throws Exception {
        Field field = entity.getClass().getDeclaredField("id");
        field.setAccessible(true);
        field.set(entity, id);
    }
}
