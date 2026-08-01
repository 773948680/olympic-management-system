package com.olympic.dakar.discipline.dto;

import jakarta.validation.constraints.Size;

/**
 * Tous les champs sont optionnels : seuls ceux fournis (non nuls) sont appliqués (PATCH).
 */
public record DisciplinePatchRequest(

        @Size(max = 100, message = "Le nom ne peut pas dépasser 100 caractères")
        String name,

        @Size(max = 500, message = "La description ne peut pas dépasser 500 caractères")
        String description
) {
}
