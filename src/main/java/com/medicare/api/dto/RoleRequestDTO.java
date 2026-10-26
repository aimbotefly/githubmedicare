package com.medicare.api.dto;

import jakarta.validation.constraints.NotBlank;

public record RoleRequestDTO(@NotBlank(message = "nom est obligatoire") String nom) {
}
