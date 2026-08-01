package com.olympic.dakar.athlete.dto;

import com.olympic.dakar.athlete.Gender;

import java.time.Instant;
import java.time.LocalDate;

public record AthleteResponse(
        Long id,
        String firstName,
        String lastName,
        Gender gender,
        LocalDate dateOfBirth,
        String nationality,
        Long disciplineId,
        String disciplineName,
        Integer height,
        Double weight,
        Instant createdAt,
        Instant updatedAt
) {
}
