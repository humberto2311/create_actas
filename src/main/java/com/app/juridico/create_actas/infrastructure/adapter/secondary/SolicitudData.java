package com.app.juridico.create_actas.infrastructure.adapter.secondary;


import java.time.LocalDate;

public record SolicitudData(
        LocalDate date,
        String names,
        String lastNames,
        String identity,
        String conduct,
        Integer radicado,     // ← 6to parámetro: Integer
        String fiscal,        // ← 7mo parámetro: String
        String typeAudience,  // ← 8vo parámetro: String
        String fact,          // ← 9no parámetro: String
        String juzgado,
        Boolean state
) {}