package com.olympic.dakar.discipline;

import com.olympic.dakar.discipline.dto.DisciplineRequest;
import com.olympic.dakar.discipline.dto.DisciplineResponse;

public final class DisciplineMapper {

    private DisciplineMapper() {
    }

    public static DisciplineResponse toResponse(Discipline discipline) {
        return new DisciplineResponse(discipline.getId(), discipline.getName(), discipline.getDescription());
    }

    public static Discipline toEntity(DisciplineRequest request) {
        return new Discipline(request.name(), request.description());
    }
}
