package com.app.juridico.create_actas.infrastructure.dto;

import lombok.Data;

import java.time.LocalDate;


@Data
public class DocumentProcess {

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
    String  state;// si o no




}
