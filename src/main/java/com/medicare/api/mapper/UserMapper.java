package com.medicare.api.mapper;

import com.medicare.api.dto.UserRequestDTO;
import com.medicare.api.dto.UserResponseDTO;
import com.medicare.api.entities.AppRole;
import com.medicare.api.entities.AppUser;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "roles", ignore = true)
    AppUser toEntity(UserRequestDTO dto);

    UserResponseDTO toDto(AppUser user);

    default String roleToName(AppRole role) {
        return role.getNom();
    }
}
