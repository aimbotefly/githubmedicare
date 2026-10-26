package com.medicare.api.controller;

import com.medicare.api.dto.AddRoleRequestDTO;
import com.medicare.api.dto.RoleRequestDTO;
import com.medicare.api.dto.RoleResponseDTO;
import com.medicare.api.dto.UserRequestDTO;
import com.medicare.api.dto.UserResponseDTO;
import com.medicare.api.service.AccountService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/accounts")
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @PostMapping("/users")
    public ResponseEntity<UserResponseDTO> createUser(@Valid @RequestBody UserRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(accountService.createUser(request));
    }

    @PostMapping("/roles")
    public ResponseEntity<RoleResponseDTO> createRole(@Valid @RequestBody RoleRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(accountService.createRole(request));
    }

    @PostMapping("/addRole")
    public UserResponseDTO addRole(@Valid @RequestBody AddRoleRequestDTO request) {
        return accountService.addRoleToUser(request);
    }
}
