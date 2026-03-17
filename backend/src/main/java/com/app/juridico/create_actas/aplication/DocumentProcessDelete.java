package com.app.juridico.create_actas.aplication;

import com.app.juridico.create_actas.domain.services.DocumentProcessDeleteService;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@AllArgsConstructor
@Component
public class DocumentProcessDelete {

    private final DocumentProcessDeleteService documentProcessService;

    public void deleteDocumentProcess(Long id) {
        documentProcessService.deleteDocumentProcess(id);
    }
}
