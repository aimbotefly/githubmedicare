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
import com.medicare.api.mapper.RoleMapperImpl;
import com.medicare.api.mapper.UserMapper;
import com.medicare.api.mapper.UserMapperImpl;
import com.medicare.api.repositories.AppRoleRepository;
import com.medicare.api.repositories.AppUserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    @Mock
    private AppUserRepository userRepository;

    @Mock
    private AppRoleRepository roleRepository;

    @Spy
    private UserMapper userMapper = new UserMapperImpl();

    @Spy
    private RoleMapper roleMapper = new RoleMapperImpl();

    @InjectMocks
    private AccountService accountService;

    @Test
    void createUser_shouldSaveUserWithoutRole() {
        UserRequestDTO request = new UserRequestDTO("abdou", "motdepasse123", "abdou@mail.com");
        when(userRepository.existsByUsername("abdou")).thenReturn(false);
        when(userRepository.save(any(AppUser.class))).thenAnswer(invocation -> {
            AppUser user = invocation.getArgument(0);
            user.setId(1L);
            return user;
        });

        UserResponseDTO result = accountService.createUser(request);

        assertEquals(1L, result.id());
        assertEquals("abdou", result.username());
        assertTrue(result.roles().isEmpty());
    }

    @Test
    void createUser_shouldThrowWhenPasswordTooShort() {
        UserRequestDTO request = new UserRequestDTO("abdou", "court", "abdou@mail.com");

        assertThrows(InvalidPasswordException.class, () -> accountService.createUser(request));
        verify(userRepository, never()).save(any(AppUser.class));
    }

    @Test
    void createUser_shouldThrowWhenUsernameTaken() {
        UserRequestDTO request = new UserRequestDTO("abdou", "motdepasse123", "abdou@mail.com");
        when(userRepository.existsByUsername("abdou")).thenReturn(true);

        assertThrows(UsernameAlreadyUsedException.class, () -> accountService.createUser(request));
        verify(userRepository, never()).save(any(AppUser.class));
    }

    @Test
    void createRole_shouldSaveRole() {
        when(roleRepository.existsByNom("ADMIN")).thenReturn(false);
        when(roleRepository.save(any(AppRole.class))).thenAnswer(invocation -> {
            AppRole role = invocation.getArgument(0);
            role.setId(1L);
            return role;
        });

        RoleResponseDTO result = accountService.createRole(new RoleRequestDTO("ADMIN"));

        assertEquals(1L, result.id());
        assertEquals("ADMIN", result.nom());
    }

    @Test
    void createRole_shouldThrowWhenRoleExists() {
        when(roleRepository.existsByNom("ADMIN")).thenReturn(true);

        assertThrows(RoleAlreadyExistsException.class, () -> accountService.createRole(new RoleRequestDTO("ADMIN")));
        verify(roleRepository, never()).save(any(AppRole.class));
    }

    @Test
    void addRoleToUser_shouldAttachRole() {
        AppUser user = new AppUser();
        user.setId(1L);
        user.setUsername("abdou");
        AppRole role = new AppRole();
        role.setId(2L);
        role.setNom("ADMIN");
        when(userRepository.findByUsername("abdou")).thenReturn(Optional.of(user));
        when(roleRepository.findByNom("ADMIN")).thenReturn(Optional.of(role));
        when(userRepository.save(any(AppUser.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UserResponseDTO result = accountService.addRoleToUser(new AddRoleRequestDTO("abdou", "ADMIN"));

        assertEquals(1, result.roles().size());
        assertEquals("ADMIN", result.roles().get(0));
    }

    @Test
    void addRoleToUser_shouldThrowWhenUserNotFound() {
        when(userRepository.findByUsername("inconnu")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> accountService.addRoleToUser(new AddRoleRequestDTO("inconnu", "ADMIN")));
    }

    @Test
    void addRoleToUser_shouldThrowWhenRoleNotFound() {
        when(userRepository.findByUsername("abdou")).thenReturn(Optional.of(new AppUser()));
        when(roleRepository.findByNom("INCONNU")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> accountService.addRoleToUser(new AddRoleRequestDTO("abdou", "INCONNU")));
    }
}
