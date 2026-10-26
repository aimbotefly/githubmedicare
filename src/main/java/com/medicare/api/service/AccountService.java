package com.medicare.api.service;

import com.medicare.api.dto.AddRoleRequestDTO;
import com.medicare.api.dto.RoleRequestDTO;
import com.medicare.api.dto.RoleResponseDTO;
import com.medicare.api.dto.UserRequestDTO;
import com.medicare.api.dto.UserResponseDTO;
import com.medicare.api.entities.AppRole;
import com.medicare.api.entities.AppUser;
import com.medicare.api.exceptions.InvalidPasswordException;
import com.medicare.api.exceptions.ResourceNotFoundException;
import com.medicare.api.exceptions.RoleAlreadyExistsException;
import com.medicare.api.exceptions.UsernameAlreadyUsedException;
import com.medicare.api.mapper.RoleMapper;
import com.medicare.api.mapper.UserMapper;
import com.medicare.api.repositories.AppRoleRepository;
import com.medicare.api.repositories.AppUserRepository;
import org.springframework.stereotype.Service;

@Service
public class AccountService {

    private final AppUserRepository userRepository;
    private final AppRoleRepository roleRepository;
    private final UserMapper userMapper;
    private final RoleMapper roleMapper;

    public AccountService(AppUserRepository userRepository, AppRoleRepository roleRepository,
                          UserMapper userMapper, RoleMapper roleMapper) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.userMapper = userMapper;
        this.roleMapper = roleMapper;
    }

    public UserResponseDTO createUser(UserRequestDTO request) {
        if (request.password().length() < 8) {
            throw new InvalidPasswordException("Le mot de passe doit contenir au moins 8 caracteres");
        }
        if (userRepository.existsByUsername(request.username())) {
            throw new UsernameAlreadyUsedException("Username deja utilise : " + request.username());
        }
        AppUser saved = userRepository.save(userMapper.toEntity(request));
        return userMapper.toDto(saved);
    }

    public RoleResponseDTO createRole(RoleRequestDTO request) {
        if (roleRepository.existsByNom(request.nom())) {
            throw new RoleAlreadyExistsException("Role deja existant : " + request.nom());
        }
        AppRole saved = roleRepository.save(roleMapper.toEntity(request));
        return roleMapper.toDto(saved);
    }

    public UserResponseDTO addRoleToUser(AddRoleRequestDTO request) {
        AppUser user = userRepository.findByUsername(request.username())
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur introuvable : " + request.username()));
        AppRole role = roleRepository.findByNom(request.roleName())
                .orElseThrow(() -> new ResourceNotFoundException("Role introuvable : " + request.roleName()));
        user.getRoles().add(role);
        AppUser saved = userRepository.save(user);
        return userMapper.toDto(saved);
    }
}
