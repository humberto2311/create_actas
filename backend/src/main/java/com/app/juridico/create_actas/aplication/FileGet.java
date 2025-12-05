/*package com.app.juridico.create_actas.aplication;

import com.app.juridico.create_actas.domain.entities.File;
import com.app.juridico.create_actas.domain.services.FileGetService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@AllArgsConstructor
@Component
public class FileGet {
    private final FileGetService fileGetService;

    public Optional<File> getFileById(int id) {
        return fileGetService.getFileById(id);
    }
    public List<File> getAllFiles() {
        return fileGetService.findAllFile();
    }
}*/
