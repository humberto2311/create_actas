package com.app.juridico.create_actas.infrastructure.adapter;

import com.app.juridico.create_actas.domain.entities.DocumentProcess;
import com.app.juridico.create_actas.domain.services.DocumentProcessGetService;
import com.app.juridico.create_actas.repository.DocumentProcessRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@AllArgsConstructor
@Component
public class DocumentProcessGetAdapter implements DocumentProcessGetService {
    private final DocumentProcessRepository documentProcessRepository;
    @Override
    public List<DocumentProcess> getAllDocumentProcesses() {
        return documentProcessRepository.findAll();
    }

    @Override
    public Optional<DocumentProcess> getDocumentProcessById(Long id) {
        return documentProcessRepository.findById(id);
    }


}
