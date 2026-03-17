package com.app.juridico.create_actas.infrastructure.adapter.secondary.word;

import java.time.LocalDate;
public record SolicitudData(
        LocalDate date,
        String identity,
        String name,
        String lastname
) {}