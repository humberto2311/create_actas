package com.app.juridico.create_actas.infrastructure.api.controller;

import com.app.juridico.create_actas.aplication.FileDelete;

import com.app.juridico.create_actas.aplication.FileSave;
import com.app.juridico.create_actas.domain.entities.File;
import com.app.juridico.create_actas.mappper.FileMapper;
import com.app.juridico.create_actas.infrastructure.dto.FileDto;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@AllArgsConstructor
@RequestMapping("/api/documents")
public class FileController {
    private final FileSave fileSave;
    /*private final FileGet fileGet;*/
    private final FileDelete fileDelete;
    private final FileMapper fileMapper;

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<FileDto> uploadDocument(@RequestParam("file") MultipartFile file,
                                                  @RequestParam(value = "observations", required = false) String observations,
                                                  @RequestParam(value = "uploadedBy", required = false) String uploadedBy) {
        try {
            File savedFile = fileSave.saveFile(file, observations, uploadedBy);
            return ResponseEntity.ok(fileMapper.toDto(savedFile));
    } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
}

}


