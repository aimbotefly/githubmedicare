package com.medicare.api.dto;

import java.time.LocalDateTime;

public record ConsultationResponseDTO(Long id, LocalDateTime dateVisite, String rapport, Long patientId) {
}
