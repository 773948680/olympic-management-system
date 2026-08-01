package com.olympic.dakar.athlete.dto;

import com.olympic.dakar.athlete.Gender;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

/**
 * Tous les champs sont optionnels : seuls ceux fournis (non nuls) sont appliqués (PATCH).
 */
public record AthletePatchRequest(

        @Size(max = 100) String firstName,

        @Size(max = 100) String lastName,

        Gender gender,

        @Past LocalDate dateOfBirth,

        @Size(max = 100) String nationality,

        Long disciplineId,

        @Min(100) @Max(250) Integer height,

        @DecimalMin("20.0") @DecimalMax("300.0") Double weight
) {
}
