package com.olympic.dakar.event.dto;

import com.olympic.dakar.event.EventStatus;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

/**
 * Tous les champs sont optionnels : seuls ceux fournis (non nuls) sont appliqués (PATCH).
 */
public record EventPatchRequest(

        @Size(max = 150, message = "Le nom ne peut pas dépasser 150 caractères")
        String name,

        Long disciplineId,

        LocalDateTime eventDate,

        @Size(max = 150, message = "Le lieu ne peut pas dépasser 150 caractères")
        String venue,

        EventStatus status
) {
}
