package com.app.juridico.create_actas.infrastructure.adapter.secondary.excel;

import com.app.juridico.create_actas.infrastructure.adapter.secondary.ResourceLoaderAdapter;

import com.app.juridico.create_actas.infrastructure.adapter.secondary.utils.GeneratedDocument;
import com.app.juridico.create_actas.infrastructure.adapter.secondary.utils.SolicitudData;
import lombok.AllArgsConstructor;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Row;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Optional;

@Service

@AllArgsConstructor
public class ExcelSolicitudServicioGeneratorAdapter {

    private final ResourceLoaderAdapter resourceLoaderAdapter;

    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public GeneratedDocument generateSolicitudServicioExcel(SolicitudData data) {
        String names = Optional.ofNullable(data.names()).orElse("").replaceAll("\\s+", " ").toUpperCase();
        String lastNames = Optional.ofNullable(data.lastNames()).orElse("").replaceAll("\\s+", " ").toUpperCase();
        String fullName = names + " " + lastNames;

        try (Workbook workbook = resourceLoaderAdapter.createSolicitudServicioWorkbookFromTemplate()) {

            // Hoja 1: SOLICITUD SERVICIO
            Sheet sheetSolicitud = workbook.getSheetAt(0);
            populateSolicitudSheet(sheetSolicitud, data, fullName,names,lastNames);

            // Hoja 2: ESTRATEGÍA JURÍDICA
            Sheet sheetEstrategia = workbook.getSheetAt(1);
            populateEstrategiaSheet(sheetEstrategia, data, fullName);

            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            workbook.write(bos);

            String filename = String.format("SOLICITUD SERVICIO Y ESTRATEGIA JURIDICA %s %s.xlsx", 
                    names, lastNames).toUpperCase();

            return new GeneratedDocument(bos.toByteArray(), filename);

        } catch (IOException e) {
            throw new RuntimeException("Failed to generate Solicitud Servicio Excel document.", e);
        }
    }

    private void populateSolicitudSheet(Sheet sheet, SolicitudData data, String fullName, String names, String lastNames) {

        String[] apellidos = lastNames.split(" ");
        String primerApellido = apellidos.length > 0 ? apellidos[0] : "";
        String segundoApellido = apellidos.length > 1 ? apellidos[1] : "";

        // Dividir los nombres
        String[] nombres = names.split(" ");
        String primerNombre = nombres.length > 0 ? nombres[0] : "";
        String segundoNombre = nombres.length > 1 ? nombres[1] : "";

        sheet.getRow(87).getCell(3).setCellValue(data.conduct());
        sheet.getRow(7).getCell(7).setCellValue(data.date());
        sheet.getRow(89).getCell(5).setCellValue(data.radicado());
        sheet.getRow(91).getCell(3).setCellValue(data.fiscal());
        sheet.getRow(91).getCell(9).setCellValue(data.juzgado());
        sheet.getRow(94).getCell(12).setCellValue(data.date());
        sheet.getRow(36).getCell(3).setCellValue(primerApellido);
        sheet.getRow(36).getCell(11).setCellValue(segundoApellido);
        sheet.getRow(36).getCell(18).setCellValue(primerNombre);
        sheet.getRow(36).getCell(25).setCellValue(segundoNombre);
        sheet.getRow(97).getCell(5).setCellValue(fullName);

    }

    private void populateEstrategiaSheet(Sheet sheet, SolicitudData data, String fullName) {
        sheet.getRow(8).getCell(2).setCellValue(fullName);
        sheet.getRow(12).getCell(4).setCellValue(data.conduct());
        sheet.getRow(14).getCell(3).setCellValue(data.radicado());
        sheet.getRow(14).getCell(8).setCellValue(data.fiscal());
        sheet.getRow(14).getCell(11).setCellValue(data.juzgado());
        sheet.getRow(16).getCell(13).setCellValue(data.date());


    }


}