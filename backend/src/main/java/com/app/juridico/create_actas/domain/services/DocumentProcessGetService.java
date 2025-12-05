package com.app.juridico.create_actas.domain.services;

import com.app.juridico.create_actas.domain.entities.DocumentProcess;

import java.util.List;
import java.util.Optional;

public interface DocumentProcessGetService {
    List<DocumentProcess> getAllDocumentProcesses();
    Optional<DocumentProcess> getDocumentProcessById(Long id);
}
