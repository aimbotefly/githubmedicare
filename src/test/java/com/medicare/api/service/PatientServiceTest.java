package com.medicare.api.service;

import com.medicare.api.dto.PatientRequestDTO;
import com.medicare.api.dto.PatientResponseDTO;
import com.medicare.api.entities.Patient;
import com.medicare.api.exceptions.EmailAlreadyUsedException;
import com.medicare.api.exceptions.ResourceNotFoundException;
import com.medicare.api.mapper.PatientMapper;
import com.medicare.api.mapper.PatientMapperImpl;
import com.medicare.api.repositories.PatientRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PatientServiceTest {

    @Mock
    private PatientRepository patientRepository;

    @Spy
    private PatientMapper patientMapper = new PatientMapperImpl();

    @InjectMocks
    private PatientService patientService;

    private Patient buildPatient() {
        Patient patient = new Patient();
        patient.setId(1L);
        patient.setNom("Alaoui");
        patient.setPrenom("Sara");
        patient.setEmail("sara@mail.com");
        patient.setTelephone("0600000000");
        return patient;
    }

    @Test
    void createPatient_shouldSavePatient() {
        PatientRequestDTO request = new PatientRequestDTO("Alaoui", "Sara", "sara@mail.com", "0600000000");
        when(patientRepository.existsByEmail("sara@mail.com")).thenReturn(false);
        when(patientRepository.save(any(Patient.class))).thenReturn(buildPatient());

        PatientResponseDTO result = patientService.createPatient(request);

        assertEquals(1L, result.id());
        assertEquals("sara@mail.com", result.email());
        verify(patientRepository).save(any(Patient.class));
    }

    @Test
    void createPatient_shouldThrowWhenEmailExists() {
        PatientRequestDTO request = new PatientRequestDTO("Alaoui", "Sara", "sara@mail.com", "0600000000");
        when(patientRepository.existsByEmail("sara@mail.com")).thenReturn(true);

        assertThrows(EmailAlreadyUsedException.class, () -> patientService.createPatient(request));
        verify(patientRepository, never()).save(any(Patient.class));
    }

    @Test
    void getAllPatients_shouldReturnList() {
        when(patientRepository.findAll()).thenReturn(List.of(buildPatient()));

        List<PatientResponseDTO> result = patientService.getAllPatients();

        assertEquals(1, result.size());
        assertEquals("Alaoui", result.get(0).nom());
    }

    @Test
    void getPatientByEmail_shouldReturnPatient() {
        when(patientRepository.findByEmail("sara@mail.com")).thenReturn(Optional.of(buildPatient()));

        PatientResponseDTO result = patientService.getPatientByEmail("sara@mail.com");

        assertEquals("Sara", result.prenom());
    }

    @Test
    void getPatientByEmail_shouldThrowWhenNotFound() {
        when(patientRepository.findByEmail("inconnu@mail.com")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> patientService.getPatientByEmail("inconnu@mail.com"));
    }
}
