package com.app.juridico.create_actas.aplication.utils.word;

import com.app.juridico.create_actas.domain.entities.DocumentProcess;
import com.app.juridico.create_actas.infrastructure.adapter.secondary.utils.GeneratedDocument;

import com.app.juridico.create_actas.infrastructure.adapter.secondary.utils.SolicitudData;
import com.app.juridico.create_actas.infrastructure.adapter.secondary.word.WordSolicitudGeneratorAdapter;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@AllArgsConstructor
@Component
public class WordSolicitud {

    private final WordSolicitudGeneratorAdapter wordSolicitudGeneratorAdapter;

    public GeneratedDocument generateSolicitudFromDocumentProcess(DocumentProcess documentProcess) {
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

        return wordSolicitudGeneratorAdapter.generateSolicitudWord(solicitudData);
    }
}