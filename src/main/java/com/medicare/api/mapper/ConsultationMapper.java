package com.medicare.api.mapper;

import com.medicare.api.dto.ConsultationRequestDTO;
import com.medicare.api.dto.ConsultationResponseDTO;
import com.medicare.api.entities.Consultation;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ConsultationMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "dateVisite", ignore = true)
    @Mapping(target = "patient", ignore = true)
    Consultation toEntity(ConsultationRequestDTO dto);

    @Mapping(source = "patient.id", target = "patientId")
    ConsultationResponseDTO toDto(Consultation consultation);
}
