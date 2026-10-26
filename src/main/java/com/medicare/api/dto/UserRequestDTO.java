package com.medicare.api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record UserRequestDTO(
        @NotBlank(message = "username est obligatoire") String username,
        @NotBlank(message = "password est obligatoire") String password,
        @NotBlank(message = "email est obligatoire") @Email(message = "email invalide") String email) {
}
