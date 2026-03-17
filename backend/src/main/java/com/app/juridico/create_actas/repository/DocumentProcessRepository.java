package com.app.juridico.create_actas.repository;

import com.app.juridico.create_actas.domain.entities.DocumentProcess;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DocumentProcessRepository extends JpaRepository<DocumentProcess, Long> {
}