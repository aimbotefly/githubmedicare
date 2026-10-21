package com.medicare.api.mapper;

import com.medicare.api.dto.PatientRequestDTO;
import com.medicare.api.dto.PatientResponseDTO;
import com.medicare.api.entities.Patient;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PatientMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "consultations", ignore = true)
    Patient toEntity(PatientRequestDTO dto);

    PatientResponseDTO toDto(Patient patient);

    List<PatientResponseDTO> toDtoList(List<Patient> patients);
}
