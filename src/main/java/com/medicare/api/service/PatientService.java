package com.medicare.api.service;

import com.medicare.api.dto.PatientRequestDTO;
import com.medicare.api.dto.PatientResponseDTO;
import com.medicare.api.entities.Patient;
import com.medicare.api.exceptions.EmailAlreadyUsedException;
import com.medicare.api.exceptions.ResourceNotFoundException;
import com.medicare.api.mapper.PatientMapper;
import com.medicare.api.repositories.PatientRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PatientService {

    private final PatientRepository patientRepository;
    private final PatientMapper patientMapper;

    public PatientService(PatientRepository patientRepository, PatientMapper patientMapper) {
        this.patientRepository = patientRepository;
        this.patientMapper = patientMapper;
    }

    public PatientResponseDTO createPatient(PatientRequestDTO request) {
        if (patientRepository.existsByEmail(request.email())) {
            throw new EmailAlreadyUsedException("Email deja utilise : " + request.email());
        }
        Patient saved = patientRepository.save(patientMapper.toEntity(request));
        return patientMapper.toDto(saved);
    }

    public List<PatientResponseDTO> getAllPatients() {
        return patientMapper.toDtoList(patientRepository.findAll());
    }

    public PatientResponseDTO getPatientByEmail(String email) {
        Patient patient = patientRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Patient introuvable avec l'email : " + email));
        return patientMapper.toDto(patient);
    }
}
