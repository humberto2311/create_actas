package com.app.juridico.create_actas.infrastructure.adapter.secondary;

import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;

@Service
public class ResourceLoaderAdapter {
    private static class ResourceNotFoundException extends RuntimeException {
        public ResourceNotFoundException(String message) {
            super(message);
        }
    }

    @Value("${app.templates.excel.acta-visita}")
    private String actaVisitaPath;

    @Value("${app.templates.excel.solicitud-servicio}")
    private String actaSoliPath;

    @Value("${app.templates.word.solicitud}")
    private String solicitudWordPath;


    private InputStream loadResource(String path, String resourceName) {
        InputStream stream = getClass().getClassLoader().getResourceAsStream(path);
        if (stream == null) {
            throw new ResourceNotFoundException(resourceName + " not found at path: " + path);
        }
        return stream;
    }

    public InputStream loadExcelTemplateStream() {
        return loadResource(actaVisitaPath, "Excel template");
    }

    public InputStream loadSolicitudExcelTemplateStream() {
        return loadResource(actaSoliPath, "Excel solicitud template");
    }

    public InputStream loadWordTemplateStream() {
        return loadResource(solicitudWordPath, "Word template");
    }


    public Workbook createActaVisitaWorkbook() {
        return createWorkbook(loadExcelTemplateStream());
    }

    public Workbook createSolicitudWorkbook() {
        return createWorkbook(loadSolicitudExcelTemplateStream());
    }

    private Workbook createWorkbook(InputStream is) {
        try (is) {
            return new XSSFWorkbook(is);
        } catch (IOException e) {
            throw new RuntimeException("Error al procesar Excel", e);
        }
    }
    public HWPFDocument createHWPFDocumentFromTemplate() {
        try (InputStream templateStream = loadWordTemplateStream()) {
            return new HWPFDocument(templateStream);
        } catch (IOException e) {
            throw new RuntimeException("Error reading or processing Word .doc template stream.", e);
        }
    }
}