package com.app.juridico.create_actas.aplication.utils;


import com.app.juridico.create_actas.infrastructure.adapter.secondary.excel.ExcelActaGeneratorAdapter;
import com.app.juridico.create_actas.infrastructure.adapter.secondary.excel.ExcelSolicitudServicioGeneratorAdapter; // Nuevo import
import com.app.juridico.create_actas.infrastructure.adapter.secondary.utils.GeneratedDocument;
import com.app.juridico.create_actas.infrastructure.adapter.secondary.utils.GeneratedDocumentsZip;
import com.app.juridico.create_actas.infrastructure.adapter.secondary.utils.SolicitudData;
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
    private final ExcelSolicitudServicioGeneratorAdapter excelSolicitudServicioGenerator; // Nuevo

    public GeneratedDocumentsZip generateDocumentsZip(SolicitudData data) {

        GeneratedDocument wordDocument = wordGenerator.generateSolicitudWord(data);
        GeneratedDocument excelActaDocument = excelGenerator.generateActaExcel(data);
        GeneratedDocument excelSolicitudDocument = excelSolicitudServicioGenerator.generateSolicitudServicioExcel(data); // Nuevo

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             ZipOutputStream zos = new ZipOutputStream(baos)) {
            ZipEntry wordEntry = new ZipEntry(wordDocument.filename());
            zos.putNextEntry(wordEntry);
            zos.write(wordDocument.content());
            zos.closeEntry();
            ZipEntry excelActaEntry = new ZipEntry(excelActaDocument.filename());
            zos.putNextEntry(excelActaEntry);
            zos.write(excelActaDocument.content());
            zos.closeEntry();

            ZipEntry excelSolicitudEntry = new ZipEntry(excelSolicitudDocument.filename());
            zos.putNextEntry(excelSolicitudEntry);
            zos.write(excelSolicitudDocument.content());
            zos.closeEntry();

            zos.finish();

            String zipFilename = String.format("DOCUMENTOS_%s_%s.zip",
                    data.names().replaceAll("\\s+", "_"),
                    data.lastNames().replaceAll("\\s+", "_")).toUpperCase();

            List<String> includedFiles = Arrays.asList(
                    wordDocument.filename(),
                    excelActaDocument.filename(),
                    excelSolicitudDocument.filename()
            );

            return new GeneratedDocumentsZip(baos.toByteArray(), zipFilename, includedFiles);

        } catch (IOException e) {
            throw new RuntimeException("Failed to create ZIP archive.", e);
        }
    }
}