package com.medicare.api.service;

import com.medicare.api.dto.ConsultationRequestDTO;
import com.medicare.api.dto.ConsultationResponseDTO;
import com.medicare.api.entities.Consultation;
import com.medicare.api.entities.Patient;
import com.medicare.api.exceptions.ResourceNotFoundException;
import com.medicare.api.mapper.ConsultationMapper;
import com.medicare.api.mapper.ConsultationMapperImpl;
import com.medicare.api.repositories.ConsultationRepository;
import com.medicare.api.repositories.PatientRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConsultationServiceTest {

    @Mock
    private ConsultationRepository consultationRepository;

    @Mock
    private PatientRepository patientRepository;

    @Spy
    private ConsultationMapper consultationMapper = new ConsultationMapperImpl();

    @InjectMocks
    private ConsultationService consultationService;

    @Test
    void addConsultation_shouldSaveConsultationForPatient() {
        Patient patient = new Patient();
        patient.setId(1L);
        ConsultationRequestDTO request = new ConsultationRequestDTO("Patient en bonne sante");
        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));
        when(consultationRepository.save(any(Consultation.class))).thenAnswer(invocation -> {
            Consultation consultation = invocation.getArgument(0);
            consultation.setId(10L);
            consultation.setDateVisite(LocalDateTime.now());
            return consultation;
        });

        ConsultationResponseDTO result = consultationService.addConsultation(1L, request);

        assertEquals(10L, result.id());
        assertEquals(1L, result.patientId());
        assertEquals("Patient en bonne sante", result.rapport());
        assertNotNull(result.dateVisite());
    }

    @Test
    void addConsultation_shouldThrowWhenPatientNotFound() {
        ConsultationRequestDTO request = new ConsultationRequestDTO("Rapport");
        when(patientRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> consultationService.addConsultation(99L, request));
        verify(consultationRepository, never()).save(any(Consultation.class));
    }
}
