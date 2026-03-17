package com.app.juridico.create_actas.infrastructure.adapter;


import com.app.juridico.create_actas.domain.entities.File;
import com.app.juridico.create_actas.domain.services.FileSaveService;
import com.app.juridico.create_actas.repository.DocumentRespository;
import lombok.AllArgsConstructor;

import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class FileSaveAdapter implements FileSaveService {
    private final DocumentRespository documentRespository;


    @Override
    public File SaveFile(File document) {
        return documentRespository.save(document);
    }

}
