package com.app.juridico.create_actas.repository;


import com.app.juridico.create_actas.domain.entities.File;
import com.app.juridico.create_actas.infrastructure.dto.FileDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DocumentRespository  extends JpaRepository<File, String> {
}
