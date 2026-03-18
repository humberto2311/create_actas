package com.app.juridico.create_actas.aplication;

import com.app.juridico.create_actas.aplication.utils.excel.ExcelActa;
import com.app.juridico.create_actas.aplication.utils.excel.ExcelSoli;
import com.app.juridico.create_actas.aplication.utils.word.WordSolicitud;

import com.app.juridico.create_actas.domain.entities.DocumentProcess;
import com.app.juridico.create_actas.domain.services.DocumentProcessGetService;
import com.app.juridico.create_actas.infrastructure.adapter.secondary.GeneratedZip;
import com.app.juridico.create_actas.infrastructure.adapter.secondary.word.GeneratedDocument;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@AllArgsConstructor
@Component
public class DocumentProcessGenerate {

    private final DocumentProcessGetService documentProcessService;
    private final WordSolicitud wordSolicitudApplication;
    private final ExcelActa excelActaApplication;
    private final ExcelSoli excelSoliApplication;

    public GeneratedZip generateBothDocumentsForDocumentProcess(Long id) {
        DocumentProcess documentProcess = documentProcessService.getDocumentProcessById(id)
                .orElseThrow(() -> new RuntimeException("DocumentProcess not found with id: " + id));

        // Generar documentos
        GeneratedDocument wordDocument = wordSolicitudApplication.generateSolicitudFromDocumentProcess(documentProcess);
        byte[] excelActaDocument = excelActaApplication.generateActaFromDocumentProcess(documentProcess);
        byte[] excelSoliDocument = excelSoliApplication.generateSolidFromDocumentProcess(documentProcess);

        // Crear el ZIP con los tres documentos
        return createZip(wordDocument, excelActaDocument, excelSoliDocument);
    }

    private GeneratedZip createZip(GeneratedDocument wordDoc, byte[] excelActaDoc, byte[] excelSoliDoc) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             ZipOutputStream zos = new ZipOutputStream(baos)) {

            // Limpiamos el nombre para que no traiga ninguna extensión previa
            String personName = extractPersonName(wordDoc.filename());

            // 1. Word
            addToZip(zos, "ACTA DE DERECHOS Y OBLIGACIONES DEL CAPTURADO " + personName + ".doc", wordDoc.content());

            // 2. Excel Acta
            addToZip(zos, "ACTA DE VISITA " + personName + ".xlsx", excelActaDoc);

            // 3. Excel Solicitud
            addToZip(zos, "SOLICITUD DEL SERVICIO Y ESTRATEGIA JURIDICA " + personName + ".xlsx", excelSoliDoc);

            zos.finish();

            String zipFilename = "DOCUMENTOS " + personName + ".zip";
            return new GeneratedZip(baos.toByteArray(), zipFilename);

        } catch (IOException e) {
            throw new RuntimeException("Failed to create ZIP file", e);
        }
    }

    // Método auxiliar para evitar repetición de código y errores de flujo
    private void addToZip(ZipOutputStream zos, String filename, byte[] content) throws IOException {
        ZipEntry entry = new ZipEntry(filename);
        zos.putNextEntry(entry);
        zos.write(content);
        zos.closeEntry();
    }

    private String extractPersonName(String filename) {
        if (filename == null) return "SIN_NOMBRE";

        // Convertimos a mayúsculas para una limpieza más segura o usamos regex
        String name = filename.toUpperCase()
                .replace("ACTA DE DERECHOS Y OBLIGACIONES DEL CAPTURADO", "")
                .replace(".DOCX", "") // Limpia extensiones modernas
                .replace(".DOC", "")  // Limpia extensiones antiguas
                .replace("_", " ");

        return name.trim();
    }
}