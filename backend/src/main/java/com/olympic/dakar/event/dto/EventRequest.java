package com.olympic.dakar.event.dto;

import com.olympic.dakar.event.EventStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public record EventRequest(

        @NotBlank(message = "Le nom de l'épreuve est obligatoire")
        @Size(max = 150, message = "Le nom ne peut pas dépasser 150 caractères")
        String name,

        @NotNull(message = "La discipline est obligatoire")
        Long disciplineId,

        @NotNull(message = "La date de l'épreuve est obligatoire")
        LocalDateTime eventDate,

        @Size(max = 150, message = "Le lieu ne peut pas dépasser 150 caractères")
        String venue,

        EventStatus status
) {
}
