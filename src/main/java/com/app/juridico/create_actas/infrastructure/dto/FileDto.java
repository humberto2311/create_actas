package com.app.juridico.create_actas.infrastructure.dto;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

import java.time.LocalDateTime;

@Data

public class FileDto {
    String id;
    String fileName;
    String fileType;
    Long fileSize;
    LocalDateTime saveDate;
    String observations;
    Integer pageCount;
    String uploadedBy;


    public String getFormattedSize() {
        if (fileSize < 1024) {
            return fileSize + " B";
        } else if (fileSize < 1024 * 1024) {
            return String.format("%.2f KB", fileSize / 1024.0);
        } else {
            return String.format("%.2f MB", fileSize/ (1024.0 * 1024.0));
        }
    }
}