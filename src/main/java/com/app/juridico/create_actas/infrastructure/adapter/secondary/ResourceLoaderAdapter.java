package com.app.juridico.create_actas.infrastructure.adapter.secondary;

import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
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
    private String solicitudServicioPath; // Nuevo

    @Value("${app.templates.word.solicitud}")
    private String solicitudWordPath;

    public InputStream loadExcelTemplateStream() {
        InputStream stream = getClass().getClassLoader()
                .getResourceAsStream(actaVisitaPath);

        if (stream == null) {
            throw new ResourceNotFoundException("Excel template not found at path: " + actaVisitaPath);
        }
        return stream;
    }

    // Nuevo método para cargar el template de solicitud de servicio
    public InputStream loadSolicitudServicioTemplateStream() {
        InputStream stream = getClass().getClassLoader()
                .getResourceAsStream(solicitudServicioPath);

        if (stream == null) {
            throw new ResourceNotFoundException("Solicitud servicio template not found at path: " + solicitudServicioPath);
        }
        return stream;
    }

    public InputStream loadWordTemplateStream() {
        InputStream stream = getClass().getClassLoader()
                .getResourceAsStream(solicitudWordPath);

        if (stream == null) {
            throw new ResourceNotFoundException("Word template not found at path: " + solicitudWordPath);
        }
        return stream;
    }

    public Workbook createWorkbookFromTemplate() {
        try (InputStream templateStream = loadExcelTemplateStream()) {
            return new XSSFWorkbook(templateStream);
        } catch (IOException e) {
            throw new RuntimeException("Error reading or processing Excel template stream.", e);
        }
    }

    // Nuevo método para crear workbook del template de solicitud de servicio
    public Workbook createSolicitudServicioWorkbookFromTemplate() {
        try (InputStream templateStream = loadSolicitudServicioTemplateStream()) {
            return new XSSFWorkbook(templateStream);
        } catch (IOException e) {
            throw new RuntimeException("Error reading or processing Solicitud Servicio Excel template stream.", e);
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