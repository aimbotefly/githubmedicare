package com.medicare.api.controller;

import com.medicare.api.entities.AppRole;
import com.medicare.api.entities.AppUser;
import com.medicare.api.repositories.AppRoleRepository;
import com.medicare.api.repositories.AppUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AccountControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AppUserRepository userRepository;

    @Autowired
    private AppRoleRepository roleRepository;

    @BeforeEach
    void cleanDatabase() {
        userRepository.deleteAll();
        roleRepository.deleteAll();
    }

    private void saveUser(String username) {
        AppUser user = new AppUser();
        user.setUsername(username);
        user.setPassword("motdepasse123");
        user.setEmail(username + "@mail.com");
        userRepository.save(user);
    }

    private void saveRole(String nom) {
        AppRole role = new AppRole();
        role.setNom(nom);
        roleRepository.save(role);
    }

    @Test
    void createUser_shouldReturn201WithoutRoleAndPassword() throws Exception {
        String body = """
                {"username":"abdou","password":"motdepasse123","email":"abdou@mail.com"}
                """;

        mockMvc.perform(post("/accounts/users").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value("abdou"))
                .andExpect(jsonPath("$.roles", hasSize(0)))
                .andExpect(jsonPath("$.password").doesNotExist());
    }

    @Test
    void createUser_shouldReturn400WhenPasswordTooShort() throws Exception {
        String body = """
                {"username":"abdou","password":"court","email":"abdou@mail.com"}
                """;

        mockMvc.perform(post("/accounts/users").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createUser_shouldReturn409WhenUsernameTaken() throws Exception {
        saveUser("abdou");
        String body = """
                {"username":"abdou","password":"motdepasse123","email":"autre@mail.com"}
                """;

        mockMvc.perform(post("/accounts/users").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isConflict());
    }

    @Test
    void createRole_shouldReturn201() throws Exception {
        mockMvc.perform(post("/accounts/roles").contentType(MediaType.APPLICATION_JSON).content("{\"nom\":\"ADMIN\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nom").value("ADMIN"));
    }

    @Test
    void createRole_shouldReturn409WhenRoleExists() throws Exception {
        saveRole("ADMIN");

        mockMvc.perform(post("/accounts/roles").contentType(MediaType.APPLICATION_JSON).content("{\"nom\":\"ADMIN\"}"))
                .andExpect(status().isConflict());
    }

    @Test
    void addRole_shouldReturn200WithRole() throws Exception {
        saveUser("abdou");
        saveRole("ADMIN");
        String body = """
                {"username":"abdou","roleName":"ADMIN"}
                """;

        mockMvc.perform(post("/accounts/addRole").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.roles", hasSize(1)))
                .andExpect(jsonPath("$.roles[0]").value("ADMIN"));
    }

    @Test
    void addRole_shouldReturn404WhenUserUnknown() throws Exception {
        saveRole("ADMIN");
        String body = """
                {"username":"inconnu","roleName":"ADMIN"}
                """;

        mockMvc.perform(post("/accounts/addRole").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isNotFound());
    }

    @Test
    void addRole_shouldReturn404WhenRoleUnknown() throws Exception {
        saveUser("abdou");
        String body = """
                {"username":"abdou","roleName":"INCONNU"}
                """;

        mockMvc.perform(post("/accounts/addRole").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isNotFound());
    }
}
