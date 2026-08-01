package com.olympic.dakar.athlete.dto;

import com.olympic.dakar.athlete.Gender;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record AthleteRequest(

        @NotBlank(message = "Le prénom est obligatoire")
        @Size(max = 100, message = "Le prénom ne peut pas dépasser 100 caractères")
        String firstName,

        @NotBlank(message = "Le nom est obligatoire")
        @Size(max = 100, message = "Le nom ne peut pas dépasser 100 caractères")
        String lastName,

        @NotNull(message = "Le sexe est obligatoire")
        Gender gender,

        @NotNull(message = "La date de naissance est obligatoire")
        @Past(message = "La date de naissance doit être dans le passé")
        LocalDate dateOfBirth,

        @NotBlank(message = "La nationalité est obligatoire")
        @Size(max = 100, message = "La nationalité ne peut pas dépasser 100 caractères")
        String nationality,

        @NotNull(message = "La discipline est obligatoire")
        Long disciplineId,

        @NotNull(message = "La taille est obligatoire")
        @Min(value = 100, message = "La taille doit être réaliste (>= 100 cm)")
        @Max(value = 250, message = "La taille doit être réaliste (<= 250 cm)")
        Integer height,

        @NotNull(message = "Le poids est obligatoire")
        @DecimalMin(value = "20.0", message = "Le poids doit être réaliste (>= 20 kg)")
        @DecimalMax(value = "300.0", message = "Le poids doit être réaliste (<= 300 kg)")
        Double weight
) {
}
