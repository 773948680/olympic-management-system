package com.olympic.dakar.result;

import com.olympic.dakar.athlete.Athlete;
import com.olympic.dakar.athlete.AthleteRepository;
import com.olympic.dakar.common.exception.BusinessRuleViolationException;
import com.olympic.dakar.common.exception.ConflictException;
import com.olympic.dakar.common.exception.ResourceNotFoundException;
import com.olympic.dakar.event.Event;
import com.olympic.dakar.event.EventRepository;
import com.olympic.dakar.result.dto.ResultRequest;
import com.olympic.dakar.result.dto.ResultResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ResultServiceImpl implements ResultService {

    private final ResultRepository resultRepository;
    private final EventRepository eventRepository;
    private final AthleteRepository athleteRepository;

    public ResultServiceImpl(ResultRepository resultRepository, EventRepository eventRepository,
                              AthleteRepository athleteRepository) {
        this.resultRepository = resultRepository;
        this.eventRepository = eventRepository;
        this.athleteRepository = athleteRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ResultResponse> findByEvent(Long eventId) {
        getEventOrThrow(eventId);
        return resultRepository.findByEventIdOrderByPositionAsc(eventId).stream()
                .map(ResultMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ResultResponse> findByAthlete(Long athleteId) {
        getAthleteOrThrow(athleteId);
        return resultRepository.findByAthleteId(athleteId).stream()
                .map(ResultMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ResultResponse> findMedalHistoryByNationality(String nationality) {
        return resultRepository.findMedalHistoryByNationality(nationality).stream()
                .map(ResultMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ResultResponse> findPodium(Long eventId) {
        getEventOrThrow(eventId);
        return resultRepository.findByEventIdOrderByPositionAsc(eventId).stream()
                .filter(result -> result.getMedal() != MedalType.NONE)
                .map(ResultMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ResultResponse findById(Long id) {
        return ResultMapper.toResponse(getOrThrow(id));
    }

    @Override
    public ResultResponse create(ResultRequest request) {
        Event event = getEventOrThrow(request.eventId());
        Athlete athlete = getAthleteOrThrow(request.athleteId());
        validateSameDiscipline(event, athlete);

        if (resultRepository.existsByEventIdAndAthleteId(event.getId(), athlete.getId())) {
            throw new ConflictException("Cet athlète a déjà un résultat enregistré pour cette épreuve");
        }
        if (resultRepository.existsByEventIdAndPosition(event.getId(), request.position())) {
            throw new ConflictException("Cette position est déjà attribuée pour cette épreuve");
        }

        Result result = new Result(event, athlete, request.position(), request.time(), request.score());
        return ResultMapper.toResponse(resultRepository.save(result));
    }

    @Override
    public ResultResponse update(Long id, ResultRequest request) {
        Result result = getOrThrow(id);
        Event event = getEventOrThrow(request.eventId());
        Athlete athlete = getAthleteOrThrow(request.athleteId());
        validateSameDiscipline(event, athlete);

        resultRepository.findByEventIdAndAthleteId(event.getId(), athlete.getId())
                .filter(existing -> !existing.getId().equals(id))
                .ifPresent(existing -> {
                    throw new ConflictException("Cet athlète a déjà un résultat enregistré pour cette épreuve");
                });
        resultRepository.findByEventIdAndPosition(event.getId(), request.position())
                .filter(existing -> !existing.getId().equals(id))
                .ifPresent(existing -> {
                    throw new ConflictException("Cette position est déjà attribuée pour cette épreuve");
                });

        result.setEvent(event);
        result.setAthlete(athlete);
        result.setTime(request.time());
        result.setScore(request.score());
        result.setPosition(request.position());
        return ResultMapper.toResponse(result);
    }

    @Override
    public void delete(Long id) {
        resultRepository.delete(getOrThrow(id));
    }

    private void validateSameDiscipline(Event event, Athlete athlete) {
        if (!event.getDiscipline().getId().equals(athlete.getDiscipline().getId())) {
            throw new BusinessRuleViolationException(
                    "L'athlète '" + athlete.getFirstName() + " " + athlete.getLastName() +
                            "' ne concourt pas dans la discipline de cette épreuve");
        }
    }

    private Result getOrThrow(Long id) {
        return resultRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Résultat introuvable avec l'id " + id));
    }

    private Event getEventOrThrow(Long eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Épreuve introuvable avec l'id " + eventId));
    }

    private Athlete getAthleteOrThrow(Long athleteId) {
        return athleteRepository.findById(athleteId)
                .orElseThrow(() -> new ResourceNotFoundException("Athlète introuvable avec l'id " + athleteId));
    }
}
