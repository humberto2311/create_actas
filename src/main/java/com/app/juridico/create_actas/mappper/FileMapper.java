package com.app.juridico.create_actas.mappper;

import com.app.juridico.create_actas.domain.entities.File;
import com.app.juridico.create_actas.infrastructure.dto.FileDto;
import org.mapstruct.Mapper;


@Mapper(componentModel = "spring")
public interface FileMapper {


    FileDto toDto(File file);


    File toEntity(FileDto fileDto);
}