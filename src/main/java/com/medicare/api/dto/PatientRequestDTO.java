package com.medicare.api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record PatientRequestDTO(
        @NotBlank(message = "nom est obligatoire") String nom,
        @NotBlank(message = "prenom est obligatoire") String prenom,
        @NotBlank(message = "email est obligatoire") @Email(message = "email invalide") String email,
        String telephone) {
}
