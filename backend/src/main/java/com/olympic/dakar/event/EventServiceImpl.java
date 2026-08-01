package com.olympic.dakar.event;

import com.olympic.dakar.common.exception.ResourceNotFoundException;
import com.olympic.dakar.discipline.Discipline;
import com.olympic.dakar.discipline.DisciplineRepository;
import com.olympic.dakar.event.dto.EventPatchRequest;
import com.olympic.dakar.event.dto.EventRequest;
import com.olympic.dakar.event.dto.EventResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@Transactional
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final DisciplineRepository disciplineRepository;

    public EventServiceImpl(EventRepository eventRepository, DisciplineRepository disciplineRepository) {
        this.eventRepository = eventRepository;
        this.disciplineRepository = disciplineRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<EventResponse> search(Long disciplineId, LocalDate date, Pageable pageable) {
        return eventRepository.findAll(EventSpecifications.withCriteria(disciplineId, date), pageable)
                .map(EventMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public EventResponse findById(Long id) {
        return EventMapper.toResponse(getOrThrow(id));
    }

    @Override
    public EventResponse create(EventRequest request) {
        Discipline discipline = getDisciplineOrThrow(request.disciplineId());
        Event event = new Event(request.name(), discipline, request.eventDate(), request.venue(), request.status());
        return EventMapper.toResponse(eventRepository.save(event));
    }

    @Override
    public EventResponse update(Long id, EventRequest request) {
        Event event = getOrThrow(id);
        Discipline discipline = getDisciplineOrThrow(request.disciplineId());
        event.setName(request.name());
        event.setDiscipline(discipline);
        event.setEventDate(request.eventDate());
        event.setVenue(request.venue());
        event.setStatus(request.status() != null ? request.status() : event.getStatus());
        return EventMapper.toResponse(event);
    }

    @Override
    public EventResponse patch(Long id, EventPatchRequest request) {
        Event event = getOrThrow(id);
        if (request.name() != null) {
            event.setName(request.name());
        }
        if (request.disciplineId() != null) {
            event.setDiscipline(getDisciplineOrThrow(request.disciplineId()));
        }
        if (request.eventDate() != null) {
            event.setEventDate(request.eventDate());
        }
        if (request.venue() != null) {
            event.setVenue(request.venue());
        }
        if (request.status() != null) {
            event.setStatus(request.status());
        }
        return EventMapper.toResponse(event);
    }

    @Override
    public void delete(Long id) {
        Event event = getOrThrow(id);
        eventRepository.delete(event);
        eventRepository.flush();
    }

    private Event getOrThrow(Long id) {
        return eventRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Épreuve introuvable avec l'id " + id));
    }

    private Discipline getDisciplineOrThrow(Long disciplineId) {
        return disciplineRepository.findById(disciplineId)
                .orElseThrow(() -> new ResourceNotFoundException("Discipline introuvable avec l'id " + disciplineId));
    }
}
