package com.medicare.api.dto;

import jakarta.validation.constraints.NotBlank;

public record AddRoleRequestDTO(
        @NotBlank(message = "username est obligatoire") String username,
        @NotBlank(message = "roleName est obligatoire") String roleName) {
}
