package com.app.juridico.create_actas.aplication;

import com.app.juridico.create_actas.domain.entities.DocumentProcess;

import com.app.juridico.create_actas.domain.services.DocumentProcessGetService;
import com.app.juridico.create_actas.domain.services.DocumentProcessSaveService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@AllArgsConstructor
@Component
public class DocumentProcessSave {
    
    private final DocumentProcessSaveService documentProcessService;


    public DocumentProcess saveDocumentProcess(DocumentProcess documentProcess) {
        return documentProcessService.saveDocumentProcess(documentProcess);
    }



}