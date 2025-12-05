package com.app.juridico.create_actas.infrastructure.adapter.secondary.utils;

import java.util.List;

public record GeneratedDocumentsZip(
    byte[] zipContent,
    String filename,
    List<String> includedFiles
) {}