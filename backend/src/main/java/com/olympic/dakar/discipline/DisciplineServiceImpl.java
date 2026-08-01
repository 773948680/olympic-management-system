package com.olympic.dakar.discipline;

import com.olympic.dakar.common.exception.ConflictException;
import com.olympic.dakar.common.exception.ResourceNotFoundException;
import com.olympic.dakar.discipline.dto.DisciplinePatchRequest;
import com.olympic.dakar.discipline.dto.DisciplineRequest;
import com.olympic.dakar.discipline.dto.DisciplineResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class DisciplineServiceImpl implements DisciplineService {

    private final DisciplineRepository disciplineRepository;

    public DisciplineServiceImpl(DisciplineRepository disciplineRepository) {
        this.disciplineRepository = disciplineRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<DisciplineResponse> findAll(Pageable pageable) {
        return disciplineRepository.findAll(pageable).map(DisciplineMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public DisciplineResponse findById(Long id) {
        return DisciplineMapper.toResponse(getOrThrow(id));
    }

    @Override
    public DisciplineResponse create(DisciplineRequest request) {
        if (disciplineRepository.existsByNameIgnoreCase(request.name())) {
            throw new ConflictException("Une discipline nommée '" + request.name() + "' existe déjà");
        }
        Discipline saved = disciplineRepository.save(DisciplineMapper.toEntity(request));
        return DisciplineMapper.toResponse(saved);
    }

    @Override
    public DisciplineResponse update(Long id, DisciplineRequest request) {
        Discipline discipline = getOrThrow(id);
        disciplineRepository.findByNameIgnoreCase(request.name())
                .filter(existing -> !existing.getId().equals(id))
                .ifPresent(existing -> {
                    throw new ConflictException("Une discipline nommée '" + request.name() + "' existe déjà");
                });
        discipline.setName(request.name());
        discipline.setDescription(request.description());
        return DisciplineMapper.toResponse(discipline);
    }

    @Override
    public DisciplineResponse patch(Long id, DisciplinePatchRequest request) {
        Discipline discipline = getOrThrow(id);
        if (request.name() != null) {
            disciplineRepository.findByNameIgnoreCase(request.name())
                    .filter(existing -> !existing.getId().equals(id))
                    .ifPresent(existing -> {
                        throw new ConflictException("Une discipline nommée '" + request.name() + "' existe déjà");
                    });
            discipline.setName(request.name());
        }
        if (request.description() != null) {
            discipline.setDescription(request.description());
        }
        return DisciplineMapper.toResponse(discipline);
    }

    @Override
    public void delete(Long id) {
        Discipline discipline = getOrThrow(id);
        disciplineRepository.delete(discipline);
        disciplineRepository.flush();
    }

    private Discipline getOrThrow(Long id) {
        return disciplineRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Discipline introuvable avec l'id " + id));
    }
}
