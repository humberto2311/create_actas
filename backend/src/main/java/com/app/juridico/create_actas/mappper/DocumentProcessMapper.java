package com.app.juridico.create_actas.mappper;

import com.app.juridico.create_actas.domain.entities.DocumentProcess;

import com.app.juridico.create_actas.infrastructure.dto.DocumentProcessDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface DocumentProcessMapper {
    
    @Mapping(source = "state", target = "state")
    DocumentProcessDto toDto(DocumentProcess documentProcess);
    
    @Mapping(source = "state", target = "state")
    DocumentProcess toEntity(DocumentProcessDto documentProcessDto);
}