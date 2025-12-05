package com.app.juridico.create_actas.aplication;

import com.app.juridico.create_actas.aplication.utils.word.WordSolicitud;
import com.app.juridico.create_actas.domain.entities.DocumentProcess;
import com.app.juridico.create_actas.domain.services.DocumentProcessGetService;
import com.app.juridico.create_actas.infrastructure.adapter.secondary.word.GeneratedDocument;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@AllArgsConstructor
@Component
public class DocumentProcessGenerate {
    
    private final DocumentProcessGetService documentProcessService;
    private final WordSolicitud wordSolicitudApplication;


    public GeneratedDocument generateSolicitudForDocumentProcess(Long id) {
        DocumentProcess documentProcess = documentProcessService.getDocumentProcessById(id)
                .orElseThrow(() -> new RuntimeException("DocumentProcess not found with id: " + id));
        
        return wordSolicitudApplication.generateSolicitudFromDocumentProcess(documentProcess);
    }
}