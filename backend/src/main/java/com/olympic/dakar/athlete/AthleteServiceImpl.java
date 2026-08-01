package com.olympic.dakar.athlete;

import com.olympic.dakar.athlete.dto.AthletePatchRequest;
import com.olympic.dakar.athlete.dto.AthleteRequest;
import com.olympic.dakar.athlete.dto.AthleteResponse;
import com.olympic.dakar.common.exception.ResourceNotFoundException;
import com.olympic.dakar.discipline.Discipline;
import com.olympic.dakar.discipline.DisciplineRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@Transactional
public class AthleteServiceImpl implements AthleteService {

    private final AthleteRepository athleteRepository;
    private final DisciplineRepository disciplineRepository;

    public AthleteServiceImpl(AthleteRepository athleteRepository, DisciplineRepository disciplineRepository) {
        this.athleteRepository = athleteRepository;
        this.disciplineRepository = disciplineRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AthleteResponse> search(String lastName, String firstName, Gender gender, String nationality,
                                         Long disciplineId, LocalDate bornAfter, LocalDate bornBefore,
                                         Pageable pageable) {
        return athleteRepository
                .findAll(AthleteSpecifications.withCriteria(lastName, firstName, gender, nationality,
                        disciplineId, bornAfter, bornBefore), pageable)
                .map(AthleteMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AthleteResponse> findByDiscipline(Long disciplineId, Pageable pageable) {
        getDisciplineOrThrow(disciplineId);
        return athleteRepository.findByDisciplineId(disciplineId, pageable).map(AthleteMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public AthleteResponse findById(Long id) {
        return AthleteMapper.toResponse(getOrThrow(id));
    }

    @Override
    public AthleteResponse create(AthleteRequest request) {
        Discipline discipline = getDisciplineOrThrow(request.disciplineId());
        Athlete athlete = new Athlete(
                request.firstName(), request.lastName(), request.gender(), request.dateOfBirth(),
                request.nationality(), discipline, request.height(), request.weight()
        );
        return AthleteMapper.toResponse(athleteRepository.save(athlete));
    }

    @Override
    public AthleteResponse update(Long id, AthleteRequest request) {
        Athlete athlete = getOrThrow(id);
        Discipline discipline = getDisciplineOrThrow(request.disciplineId());
        athlete.setFirstName(request.firstName());
        athlete.setLastName(request.lastName());
        athlete.setGender(request.gender());
        athlete.setDateOfBirth(request.dateOfBirth());
        athlete.setNationality(request.nationality());
        athlete.setDiscipline(discipline);
        athlete.setHeight(request.height());
        athlete.setWeight(request.weight());
        return AthleteMapper.toResponse(athlete);
    }

    @Override
    public AthleteResponse patch(Long id, AthletePatchRequest request) {
        Athlete athlete = getOrThrow(id);
        if (request.firstName() != null) {
            athlete.setFirstName(request.firstName());
        }
        if (request.lastName() != null) {
            athlete.setLastName(request.lastName());
        }
        if (request.gender() != null) {
            athlete.setGender(request.gender());
        }
        if (request.dateOfBirth() != null) {
            athlete.setDateOfBirth(request.dateOfBirth());
        }
        if (request.nationality() != null) {
            athlete.setNationality(request.nationality());
        }
        if (request.disciplineId() != null) {
            athlete.setDiscipline(getDisciplineOrThrow(request.disciplineId()));
        }
        if (request.height() != null) {
            athlete.setHeight(request.height());
        }
        if (request.weight() != null) {
            athlete.setWeight(request.weight());
        }
        return AthleteMapper.toResponse(athlete);
    }

    @Override
    public void delete(Long id) {
        athleteRepository.delete(getOrThrow(id));
    }

    private Athlete getOrThrow(Long id) {
        return athleteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Athlète introuvable avec l'id " + id));
    }

    private Discipline getDisciplineOrThrow(Long disciplineId) {
        return disciplineRepository.findById(disciplineId)
                .orElseThrow(() -> new ResourceNotFoundException("Discipline introuvable avec l'id " + disciplineId));
    }
}
