package com.olympic.dakar.athlete;

import com.olympic.dakar.athlete.dto.AthleteResponse;
import com.olympic.dakar.discipline.Discipline;

public final class AthleteMapper {

    private AthleteMapper() {
    }

    public static AthleteResponse toResponse(Athlete athlete) {
        Discipline discipline = athlete.getDiscipline();
        return new AthleteResponse(
                athlete.getId(),
                athlete.getFirstName(),
                athlete.getLastName(),
                athlete.getGender(),
                athlete.getDateOfBirth(),
                athlete.getNationality(),
                discipline.getId(),
                discipline.getName(),
                athlete.getHeight(),
                athlete.getWeight(),
                athlete.getCreatedAt(),
                athlete.getUpdatedAt()
        );
    }
}
