package com.medicare.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ConsultationRequestDTO(
        @NotBlank(message = "rapport est obligatoire")
        @Size(max = 3000, message = "rapport ne doit pas depasser 3000 caracteres")
        String rapport) {
}
