package com.medicare.api.mapper;

import com.medicare.api.dto.RoleRequestDTO;
import com.medicare.api.dto.RoleResponseDTO;
import com.medicare.api.entities.AppRole;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface RoleMapper {

    @Mapping(target = "id", ignore = true)
    AppRole toEntity(RoleRequestDTO dto);

    RoleResponseDTO toDto(AppRole role);
}
