package com.app.juridico.create_actas.domain.services;

import com.app.juridico.create_actas.domain.entities.File;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;



public interface FileSaveService {

File SaveFile(File document);
}
