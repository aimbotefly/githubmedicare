package com.medicare.api.service;

import com.medicare.api.dto.ConsultationRequestDTO;
import com.medicare.api.dto.ConsultationResponseDTO;
import com.medicare.api.entities.Consultation;
import com.medicare.api.entities.Patient;
import com.medicare.api.exceptions.ResourceNotFoundException;
import com.medicare.api.mapper.ConsultationMapper;
import com.medicare.api.repositories.ConsultationRepository;
import com.medicare.api.repositories.PatientRepository;
import org.springframework.stereotype.Service;

@Service
public class ConsultationService {

    private final ConsultationRepository consultationRepository;
    private final PatientRepository patientRepository;
    private final ConsultationMapper consultationMapper;

    public ConsultationService(ConsultationRepository consultationRepository,
                               PatientRepository patientRepository,
                               ConsultationMapper consultationMapper) {
        this.consultationRepository = consultationRepository;
        this.patientRepository = patientRepository;
        this.consultationMapper = consultationMapper;
    }

    public ConsultationResponseDTO addConsultation(Long idPatient, ConsultationRequestDTO request) {
        Patient patient = patientRepository.findById(idPatient)
                .orElseThrow(() -> new ResourceNotFoundException("Patient introuvable avec l'id : " + idPatient));
        Consultation consultation = consultationMapper.toEntity(request);
        consultation.setPatient(patient);
        Consultation saved = consultationRepository.save(consultation);
        return consultationMapper.toDto(saved);
    }
}
