package com.app.juridico.create_actas.aplication;

import com.app.juridico.create_actas.aplication.utils.DocumentPageCounter;
import com.app.juridico.create_actas.aplication.utils.FileTypeUtils;
import com.app.juridico.create_actas.domain.entities.File;
import com.app.juridico.create_actas.domain.services.FileSaveService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;

@AllArgsConstructor
@Component
public class FileSave {
    private final FileSaveService fileSaveService;


    public File saveFile(MultipartFile  file, String observations, String uploadedBy) throws IOException {
        File document = new File();
        document.setFileName(file.getOriginalFilename());
        document.setFileType(FileTypeUtils.getFileType(file.getOriginalFilename()));
        document.setFileSize(file.getSize());
        document.setPageCount(DocumentPageCounter.countPages(file));
        document.setObservations(observations);
        document.setUploadedBy(uploadedBy);
        document.setSaveDate(LocalDateTime.now());

        return fileSaveService.SaveFile(document);

    }


}
