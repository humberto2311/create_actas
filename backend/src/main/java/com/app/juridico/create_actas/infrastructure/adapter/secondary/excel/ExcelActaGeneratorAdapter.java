package com.app.juridico.create_actas.infrastructure.adapter.secondary.excel;

import com.app.juridico.create_actas.infrastructure.adapter.secondary.ResourceLoaderAdapter;
import lombok.AllArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
@Service
@AllArgsConstructor
public class ExcelActaGeneratorAdapter {

    private final ResourceLoaderAdapter resourceLoaderAdapter;

    public byte[] generateActaExcel(ActaData data) {
        try (Workbook workbook = resourceLoaderAdapter.createActaVisitaWorkbook()) {
            Sheet sheet = workbook.getSheetAt(0);

            // Configurar formato para fecha
            CreationHelper createHelper = workbook.getCreationHelper();
            CellStyle dateStyle = workbook.createCellStyle();
            dateStyle.setDataFormat(createHelper.createDataFormat().getFormat("dd mmmm yyyy"));

            // Columna E8 y E27 - Fecha en formato: "día de mes año"
            String[] dateParts = data.date().split("-");
            if (dateParts.length == 3) {
                int day = Integer.parseInt(dateParts[2]);
                int month = Integer.parseInt(dateParts[1]);
                int year = Integer.parseInt(dateParts[0]);

                String monthName = getMonthInSpanish(month);
                String formattedDate = day + " DE " + monthName + " DE " + year;

                // E8 y E27
                setCellValue(sheet, 7, 4, formattedDate);  // E8
                setCellValue(sheet, 26, 4, formattedDate); // E27
            }

            // E9 y H64 - Nombre completo en MAYÚSCULAS
            setCellValue(sheet, 8, 4, toUpperCase(data.clientName()));  // E9
            setCellValue(sheet, 63, 7, toUpperCase(data.clientName())); // H64

            // F11 - Conducta en MAYÚSCULAS
            setCellValue(sheet, 10, 5, toUpperCase(data.conduct())); // F11

            // E13 - Juzgado en MAYÚSCULAS
            setCellValue(sheet, 12, 4, toUpperCase(data.juzgado())); // E13

            // E15 - Radicado
            if (data.radicado() != null) {
                setCellValue(sheet, 14, 4, data.radicado()); // E15
            }
            if (data.state()) {
                // Si es true, poner "SI:X" en F19
                setCellValue(sheet, 18, 5, "SI:X"); // F19
            } else {
                // Si es false, poner "NO:X" en H19
                setCellValue(sheet, 18, 7, "NO:X"); // H19
            }

            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            workbook.write(bos);
            return bos.toByteArray();

        } catch (IOException e) {
            throw new RuntimeException("Failed to generate Excel Acta document.", e);
        }
    }

    private void setCellValue(Sheet sheet, int rowIndex, int colIndex, String value) {
        Row row = sheet.getRow(rowIndex);
        if (row == null) {
            row = sheet.createRow(rowIndex);
        }
        Cell cell = row.getCell(colIndex);
        if (cell == null) {
            cell = row.createCell(colIndex);
        }
        cell.setCellValue(value);
    }

    private void setCellValue(Sheet sheet, int rowIndex, int colIndex, int value) {
        Row row = sheet.getRow(rowIndex);
        if (row == null) {
            row = sheet.createRow(rowIndex);
        }
        Cell cell = row.getCell(colIndex);
        if (cell == null) {
            cell = row.createCell(colIndex);
        }
        cell.setCellValue(value);
    }

    private String toUpperCase(String value) {
        return value != null ? value.toUpperCase() : "";
    }

    private String getMonthInSpanish(int month) {
        String[] months = {
                "ENERO", "FEBRERO", "MARZO", "ABRIL", "MAYO", "JUNIO",
                "JULIO", "AGOSTO", "SEPTIEMBRE", "OCTUBRE", "NOVIEMBRE", "DICIEMBRE"
        };
        return months[month - 1];
    }
}