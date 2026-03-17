package com.app.juridico.create_actas.aplication;

import com.app.juridico.create_actas.domain.entities.DocumentProcess;
import com.app.juridico.create_actas.domain.services.DocumentProcessGetService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@AllArgsConstructor
@Component
public class DocumentProcessGet {

    private final DocumentProcessGetService documentProcessService;

    public List<DocumentProcess> getAllDocumentProcesses() {
        return documentProcessService.getAllDocumentProcesses();
    }

    public Optional<DocumentProcess> getDocumentProcessById(Long id) {
        return documentProcessService.getDocumentProcessById(id);
    }


}