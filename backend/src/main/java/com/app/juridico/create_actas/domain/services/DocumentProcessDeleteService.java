package com.app.juridico.create_actas.domain.services;

import com.app.juridico.create_actas.domain.entities.DocumentProcess;

import java.util.Optional;

public interface DocumentProcessDeleteService {
    Optional<DocumentProcess> deleteDocumentProcess(Long id);
}
