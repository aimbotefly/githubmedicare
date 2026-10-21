package com.medicare.api.controller;

import com.medicare.api.dto.ConsultationRequestDTO;
import com.medicare.api.dto.ConsultationResponseDTO;
import com.medicare.api.service.ConsultationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/patients/{idPatient}/consultations")
public class ConsultationController {

    private final ConsultationService consultationService;

    public ConsultationController(ConsultationService consultationService) {
        this.consultationService = consultationService;
    }

    @PostMapping
    public ResponseEntity<ConsultationResponseDTO> addConsultation(@PathVariable Long idPatient,
                                                                   @Valid @RequestBody ConsultationRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(consultationService.addConsultation(idPatient, request));
    }
}
