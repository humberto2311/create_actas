package com.app.juridico.create_actas.infrastructure.dto;

import lombok.Data;

import java.time.LocalDate;


@Data
public class DocumentProcessDto {

    Long id;
    LocalDate date;
    String names;
    String lastNames;
    String identity;
    String conduct;//crimen
    Integer radicado;
    String fiscal;
    String typeAudience;
    String fact; //hechos
    String juzgado;
    Boolean  state;// si o no




}
