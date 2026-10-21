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

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class PatientControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PatientRepository patientRepository;

    @BeforeEach
    void cleanDatabase() {
        patientRepository.deleteAll();
    }

    private void savePatient(String email) {
        Patient patient = new Patient();
        patient.setNom("Alaoui");
        patient.setPrenom("Sara");
        patient.setEmail(email);
        patient.setTelephone("0600000000");
        patientRepository.save(patient);
    }

    @Test
    void createPatient_shouldReturn201() throws Exception {
        String body = """
                {"nom":"Alaoui","prenom":"Sara","email":"sara@mail.com","telephone":"0600000000"}
                """;

        mockMvc.perform(post("/patients").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value("sara@mail.com"))
                .andExpect(jsonPath("$.id").exists());
    }

    @Test
    void createPatient_shouldReturn409WhenEmailExists() throws Exception {
        savePatient("sara@mail.com");
        String body = """
                {"nom":"Autre","prenom":"Test","email":"sara@mail.com","telephone":"0611111111"}
                """;

        mockMvc.perform(post("/patients").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isConflict());
    }

    @Test
    void createPatient_shouldReturn400WhenInvalid() throws Exception {
        String body = """
                {"nom":"","prenom":"Sara","email":"pas-un-email"}
                """;

        mockMvc.perform(post("/patients").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getAllPatients_shouldReturnList() throws Exception {
        savePatient("a@mail.com");
        savePatient("b@mail.com");

        mockMvc.perform(get("/patients"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    void getPatientByEmail_shouldReturn200() throws Exception {
        savePatient("sara@mail.com");

        mockMvc.perform(get("/patients/{email}", "sara@mail.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nom").value("Alaoui"));
    }

    @Test
    void getPatientByEmail_shouldReturn404WhenUnknown() throws Exception {
        mockMvc.perform(get("/patients/{email}", "inconnu@mail.com"))
                .andExpect(status().isNotFound());
    }
}
