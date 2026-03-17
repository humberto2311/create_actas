package com.app.juridico.create_actas.infrastructure.adapter;

import com.app.juridico.create_actas.domain.entities.DocumentProcess;
import com.app.juridico.create_actas.domain.services.DocumentProcessSaveService;
import com.app.juridico.create_actas.repository.DocumentProcessRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor

public class DocumenProcessSaveAdapter implements DocumentProcessSaveService {
    private final DocumentProcessRepository documentProcessRepository;
    @Override
    public DocumentProcess saveDocumentProcess(DocumentProcess documentProcess) {
        return documentProcessRepository.save(documentProcess);
    }
}
