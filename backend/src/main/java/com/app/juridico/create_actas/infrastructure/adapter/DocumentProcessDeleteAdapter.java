package com.app.juridico.create_actas.infrastructure.adapter;

import com.app.juridico.create_actas.domain.entities.DocumentProcess;
import com.app.juridico.create_actas.domain.services.DocumentProcessDeleteService;
import com.app.juridico.create_actas.repository.DocumentProcessRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
@Component
@AllArgsConstructor
public class DocumentProcessDeleteAdapter implements DocumentProcessDeleteService {

    private final DocumentProcessRepository documentRespository;

    @Override
    public Optional<DocumentProcess> deleteDocumentProcess(Long id) {
        // 1. Buscamos el registro para poder devolverlo (opcional, según tu interfaz)
        Optional<DocumentProcess> document = documentRespository.findById(id);

        // 2. Si existe, lo eliminamos
        document.ifPresent(documentRespository::delete);

        return document;
    }
}