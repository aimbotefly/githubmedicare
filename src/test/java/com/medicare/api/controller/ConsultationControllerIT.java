package com.medicare.api.controller;

import com.medicare.api.entities.Patient;
import com.medicare.api.repositories.PatientRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ConsultationControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PatientRepository patientRepository;

    private Long patientId;

    @BeforeEach
    void prepareData() {
        patientRepository.deleteAll();
        Patient patient = new Patient();
        patient.setNom("Alaoui");
        patient.setPrenom("Sara");
        patient.setEmail("sara@mail.com");
        patient.setTelephone("0600000000");
        patientId = patientRepository.save(patient).getId();
    }

    @Test
    void addConsultation_shouldReturn201() throws Exception {
        String body = """
                {"rapport":"Patient en bonne sante"}
                """;

        mockMvc.perform(post("/patients/{id}/consultations", patientId)
                        .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.rapport").value("Patient en bonne sante"))
                .andExpect(jsonPath("$.patientId").value(patientId))
                .andExpect(jsonPath("$.dateVisite").exists());
    }

    @Test
    void addConsultation_shouldReturn404WhenPatientUnknown() throws Exception {
        String body = """
                {"rapport":"Rapport"}
                """;

        mockMvc.perform(post("/patients/{id}/consultations", 999999L)
                        .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isNotFound());
    }

    @Test
    void addConsultation_shouldReturn400WhenRapportTooLong() throws Exception {
        String body = "{\"rapport\":\"" + "a".repeat(3001) + "\"}";

        mockMvc.perform(post("/patients/{id}/consultations", patientId)
                        .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    void addConsultation_shouldReturn400WhenRapportBlank() throws Exception {
        String body = """
                {"rapport":""}
                """;

        mockMvc.perform(post("/patients/{id}/consultations", patientId)
                        .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isBadRequest());
    }
}
