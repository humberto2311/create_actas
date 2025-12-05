package com.app.juridico.create_actas.aplication.utils;

import com.app.juridico.create_actas.infrastructure.adapter.secondary.GeneratedDocument;
import com.app.juridico.create_actas.infrastructure.adapter.secondary.GeneratedDocumentsZip;
import com.app.juridico.create_actas.infrastructure.adapter.secondary.excel.ExcelActaGeneratorAdapter;
import com.app.juridico.create_actas.infrastructure.adapter.secondary.word.WordSolicitudGeneratorAdapter;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Component
@AllArgsConstructor
public class DocumentZipGenerator {

    private final WordSolicitudGeneratorAdapter wordGenerator;
    private final ExcelActaGeneratorAdapter excelGenerator;

    public GeneratedDocumentsZip generateDocumentsZip(com.app.juridico.create_actas.infrastructure.adapter.secondary.SolicitudData data) {

        GeneratedDocument wordDocument = wordGenerator.generateSolicitudWord(data);
        GeneratedDocument excelDocument = excelGenerator.generateActaExcel(data);

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             ZipOutputStream zos = new ZipOutputStream(baos)) {

            ZipEntry wordEntry = new ZipEntry(wordDocument.filename());
            zos.putNextEntry(wordEntry);
            zos.write(wordDocument.content());
            zos.closeEntry();

            ZipEntry excelEntry = new ZipEntry(excelDocument.filename());
            zos.putNextEntry(excelEntry);
            zos.write(excelDocument.content());
            zos.closeEntry();

            zos.finish();

            String zipFilename = String.format("DOCUMENTOS_%s_%s.zip", 
                data.names().replaceAll("\\s+", "_"),
                data.lastNames().replaceAll("\\s+", "_")).toUpperCase();

            List<String> includedFiles = Arrays.asList(wordDocument.filename(), excelDocument.filename());

            return new GeneratedDocumentsZip(baos.toByteArray(), zipFilename, includedFiles);

        } catch (IOException e) {
            throw new RuntimeException("Failed to create ZIP archive.", e);
        }
    }
}