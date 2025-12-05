package com.app.juridico.create_actas.infrastructure.adapter.secondary.excel;

import com.app.juridico.create_actas.infrastructure.adapter.secondary.GeneratedDocument;
import com.app.juridico.create_actas.infrastructure.adapter.secondary.ResourceLoaderAdapter;
import com.app.juridico.create_actas.infrastructure.adapter.secondary.SolicitudData;
import lombok.AllArgsConstructor;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Row;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Optional;

@Service
@AllArgsConstructor
public class ExcelActaGeneratorAdapter {

    private final ResourceLoaderAdapter resourceLoaderAdapter;

    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern("dd 'de' MMMM 'de' yyyy", new Locale("es", "ES"));

    public GeneratedDocument generateActaExcel(SolicitudData data) {
        String names = Optional.ofNullable(data.names()).orElse("").replaceAll("\\s+", " ");
        String lastNames = Optional.ofNullable(data.lastNames()).orElse("").replaceAll("\\s+", " ");

        try (InputStream templateStream = resourceLoaderAdapter.loadExcelTemplateStream();
             Workbook workbook = resourceLoaderAdapter.createWorkbookFromTemplate()) {

            Sheet sheet = workbook.getSheetAt(0);

            String fechaFormateada = formatDate(data.date());
            sheet.getRow(7).getCell(4).setCellValue(fechaFormateada);

            sheet.getRow(8).getCell(4).setCellValue(names.toUpperCase() + " " + lastNames.toUpperCase());
            sheet.getRow(10).getCell(5).setCellValue(data.conduct());
            sheet.getRow(12).getCell(4).setCellValue(data.juzgado());
            sheet.getRow(14).getCell(4).setCellValue(data.radicado());
            Row row19 = sheet.getRow(18);

            row19.getCell(5).setCellValue("SI: ");
            row19.getCell(7).setCellValue("NO:");
            if (data.state() != null && data.state()) {
                row19.getCell(5).setCellValue("SI: X");
            } else {
                row19.getCell(7).setCellValue("NO: X");
            }

            sheet.getRow(63).getCell(7).setCellValue(names.toUpperCase() + " " + lastNames.toUpperCase());

            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            workbook.write(bos);

            String filename = String.format("ACTA DE VISITA %s %s.xlsx", names, lastNames).toUpperCase();

            return new GeneratedDocument(bos.toByteArray(), filename);

        } catch (IOException e) {
            throw new RuntimeException("Failed to generate Excel Acta document.", e);
        }
    }

    private String formatDate(LocalDate date) {
        if (date == null) {
            return "";
        }
        return date.format(DATE_FORMATTER).toUpperCase();
    }
}