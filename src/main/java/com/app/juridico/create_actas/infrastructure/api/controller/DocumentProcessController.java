package com.app.juridico.create_actas.infrastructure.api.controller;

import com.app.juridico.create_actas.aplication.DocumentProcessDelete;
import com.app.juridico.create_actas.aplication.DocumentProcessGenerate;
import com.app.juridico.create_actas.aplication.DocumentProcessGet;
import com.app.juridico.create_actas.aplication.DocumentProcessSave;
import com.app.juridico.create_actas.infrastructure.adapter.secondary.GeneratedDocumentsZip;
import com.app.juridico.create_actas.infrastructure.dto.DocumentProcessDto;
import com.app.juridico.create_actas.mappper.DocumentProcessMapper;
import lombok.AllArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/document-process")
@AllArgsConstructor
public class DocumentProcessController {

    private final DocumentProcessGet documentProcessGet;
    private final DocumentProcessSave documentProcessSave;
    private final DocumentProcessDelete documentProcessDelete;
    private final DocumentProcessMapper documentProcessMapper;
    private final DocumentProcessGenerate documentProcessGenerate;

    @GetMapping
    public ResponseEntity<List<DocumentProcessDto>> getAllDocumentProcesses() {
        List<DocumentProcessDto> documentProcesses = documentProcessGet.getAllDocumentProcesses()
                .stream()
                .map(documentProcessMapper::toDto)
                .collect(Collectors.toList());

        return ResponseEntity.ok(documentProcesses);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DocumentProcessDto> getDocumentProcessById(@PathVariable Long id) {
        return documentProcessGet.getDocumentProcessById(id)
                .map(documentProcessMapper::toDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}/generate-documents-zip")
    public ResponseEntity<Resource> generateDocumentsZip(@PathVariable Long id) {
        GeneratedDocumentsZip generatedZip = documentProcessGenerate.generateDocumentsZipForDocumentProcess(id);

        ByteArrayResource resource = new ByteArrayResource(generatedZip.zipContent());

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + generatedZip.filename() + "\"")
                .header("X-Included-Files", String.join(",", generatedZip.includedFiles()))
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .contentLength(generatedZip.zipContent().length)
                .body(resource);
    }

    @PostMapping
    public ResponseEntity<DocumentProcessDto> createDocumentProcess(
            @RequestBody DocumentProcessDto documentProcessDto) {
        var documentProcess = documentProcessMapper.toEntity(documentProcessDto);
        var saved = documentProcessSave.saveDocumentProcess(documentProcess);
        return ResponseEntity.ok(documentProcessMapper.toDto(saved));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDocumentProcess(@PathVariable Long id) {
        documentProcessDelete.deleteDocumentProcess(id);
        return ResponseEntity.noContent().build();
    }
}