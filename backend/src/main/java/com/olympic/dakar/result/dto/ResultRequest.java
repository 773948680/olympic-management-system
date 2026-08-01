package com.olympic.dakar.result.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

public record ResultRequest(

        @NotNull(message = "L'épreuve est obligatoire")
        Long eventId,

        @NotNull(message = "L'athlète est obligatoire")
        Long athleteId,

        @NotNull(message = "La position est obligatoire")
        @Min(value = 1, message = "La position doit être supérieure ou égale à 1")
        Integer position,

        @Size(max = 50, message = "Le temps ne peut pas dépasser 50 caractères")
        String time,

        @PositiveOrZero(message = "Le score doit être positif ou nul")
        Double score
) {
}
