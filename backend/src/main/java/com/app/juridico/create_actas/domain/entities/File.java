package com.app.juridico.create_actas.domain.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "documents")
@AllArgsConstructor
@NoArgsConstructor
public class File {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "File_name", nullable = false, length = 255)
    private String fileName;

    @Column(name = "File_type", nullable = false, length = 100)
    private String fileType;

    @Column(name = "File_size", nullable = false)
    private Long fileSize;

    @Column(name = "Save_date", nullable = false)
    private LocalDateTime saveDate;

    @Column(name = "Observations", nullable = true, length = 2000)
    private String observations;

    @Column(name = "uploaded_by", length = 100)
    private String uploadedBy;
    @Column(name = "page_count", nullable = false)
    private Integer pageCount;

    @PrePersist
    protected void onCreate() {
        saveDate = LocalDateTime.now();
    }

}