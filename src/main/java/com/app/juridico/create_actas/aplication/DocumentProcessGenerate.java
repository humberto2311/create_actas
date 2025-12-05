package com.app.juridico.create_actas.aplication;

import com.app.juridico.create_actas.aplication.utils.DocumentZipGenerator;
import com.app.juridico.create_actas.domain.entities.DocumentProcess;
import com.app.juridico.create_actas.domain.services.DocumentProcessGetService;
import com.app.juridico.create_actas.infrastructure.adapter.secondary.GeneratedDocumentsZip;
import com.app.juridico.create_actas.infrastructure.adapter.secondary.SolicitudData;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@AllArgsConstructor
@Component
public class DocumentProcessGenerate {

    private final DocumentProcessGetService documentProcessService;
    private final DocumentZipGenerator documentZipGenerator;

    public GeneratedDocumentsZip generateDocumentsZipForDocumentProcess(Long id) {
        DocumentProcess documentProcess = documentProcessService.getDocumentProcessById(id)
                .orElseThrow(() -> new RuntimeException("DocumentProcess not found with id: " + id));

        // Convertir DocumentProcess a SolicitudData
        SolicitudData solicitudData = new SolicitudData(
                documentProcess.getDate(),
                documentProcess.getNames(),
                documentProcess.getLastNames(),
                documentProcess.getIdentity(),
                documentProcess.getConduct(),
                documentProcess.getRadicado(),
                documentProcess.getFiscal(),
                documentProcess.getTypeAudience(),
                documentProcess.getFact(),
                documentProcess.getJuzgado(),
                documentProcess.isState()
        );

        return documentZipGenerator.generateDocumentsZip(solicitudData);
    }
}