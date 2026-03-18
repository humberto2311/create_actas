package com.app.juridico.create_actas.infrastructure.api.controller;

import com.app.juridico.create_actas.aplication.DocumentProcessDelete;
import com.app.juridico.create_actas.aplication.DocumentProcessGenerate;
import com.app.juridico.create_actas.aplication.DocumentProcessGet;
import com.app.juridico.create_actas.aplication.DocumentProcessSave;
import com.app.juridico.create_actas.infrastructure.adapter.secondary.GeneratedZip;
import com.app.juridico.create_actas.infrastructure.adapter.secondary.word.GeneratedDocument;
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


    // Mantén el endpoint original si todavía lo necesitas
    @GetMapping("/{id}/generate-solicitud")
    public ResponseEntity<Resource> generateSolicitudDocument(@PathVariable Long id) {
        GeneratedZip generatedDocument = documentProcessGenerate.generateBothDocumentsForDocumentProcess(id);

        ByteArrayResource resource = new ByteArrayResource(generatedDocument.content());

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + generatedDocument.filename() + "\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .contentLength(generatedDocument.content().length)
                .body(resource);
    }

    @PostMapping
    public ResponseEntity<DocumentProcessDto> createDocumentProcess(
            @RequestBody DocumentProcessDto documentProcessDto) {
        var documentProcess = documentProcessMapper.toEntity(documentProcessDto);
        var saved = documentProcessSave.saveDocumentProcess(documentProcess);
        return ResponseEntity.ok(documentProcessMapper.toDto(saved));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteDocumentProcess(@PathVariable Long id) {
        documentProcessDelete.deleteDocumentProcess(id);
        return ResponseEntity.noContent().build();
    }
    @PutMapping("/{id}")
    public ResponseEntity<DocumentProcessDto> updateDocumentProcess(
            @PathVariable Long id,
            @RequestBody DocumentProcessDto dto) {
        // Aseguramos que el ID del path sea el que se use
        var entity = documentProcessMapper.toEntity(dto);
        entity.setId(id);
        var updated = documentProcessSave.saveDocumentProcess(entity);
        return ResponseEntity.ok(documentProcessMapper.toDto(updated));
    }
}