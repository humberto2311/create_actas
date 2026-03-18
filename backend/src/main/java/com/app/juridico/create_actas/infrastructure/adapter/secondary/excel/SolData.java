package com.app.juridico.create_actas.infrastructure.adapter.secondary.excel;

import java.time.LocalDate;

public record SolData(String names, String lasname, String date, String conduct, String juzgado, String radicado, boolean state, String identity, String nacion, String fiscal, String fact, LocalDate captura) {
}
