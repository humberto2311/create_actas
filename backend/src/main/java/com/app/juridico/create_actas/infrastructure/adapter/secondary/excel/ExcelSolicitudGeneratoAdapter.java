package com.app.juridico.create_actas.infrastructure.adapter.secondary.excel;

import com.app.juridico.create_actas.infrastructure.adapter.secondary.ResourceLoaderAdapter;
import lombok.AllArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

@Service
@AllArgsConstructor
public class ExcelSolicitudGeneratoAdapter {

    private final ResourceLoaderAdapter resourceLoaderAdapter;
    private static final DecimalFormat IDENTITY_FORMAT;

    static {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.US);
        symbols.setGroupingSeparator(',');
        symbols.setDecimalSeparator('.');
        IDENTITY_FORMAT = new DecimalFormat("#,###", symbols);
        IDENTITY_FORMAT.setGroupingSize(3);
        IDENTITY_FORMAT.setGroupingUsed(true);
    }

    public byte[] generateSoliExcel(SolData data) {
        try (Workbook workbook = resourceLoaderAdapter.createSolicitudWorkbook()) {

            Sheet solicitudSheet = workbook.getSheetAt(0);
            fillSolicitudSheet(solicitudSheet, data);

            Sheet estrategiaSheet = workbook.getSheetAt(1);
            fillEstrategiaSheet(estrategiaSheet, data);

            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            workbook.write(bos);
            return bos.toByteArray();

        } catch (IOException e) {
            throw new RuntimeException("Failed to generate Excel Acta document.", e);
        }
    }

    private void fillSolicitudSheet(Sheet sheet, SolData data) {
       NameParts nameParts = splitNameParts(
                data.names().toUpperCase(),
                data.lasname().toUpperCase()
        );

        setCellValue(sheet, "R8", data.date().toUpperCase());
        setCellValue(sheet, "AB88",  data.captura() != null ? data.captura().toString() : "");
        setCellValue(sheet, "N95", data.date().toUpperCase());
        setCellValue(sheet, "D37", nameParts.firstLastName());  // Primer apellido
        setCellValue(sheet, "U116", nameParts.firstLastName()); // Primer apellido
        setCellValue(sheet, "L37", nameParts.secondLastName()); // Segundo apellido
        setCellValue(sheet, "Z116", nameParts.secondLastName()); // Segundo apellido
        setCellValue(sheet, "S37", nameParts.firstName());   // Primer nombre
        setCellValue(sheet, "P116", nameParts.firstName());  // Primer nombre
        setCellValue(sheet, "Z37", nameParts.secondName());  // Segundo nombre
        setCellValue(sheet, "YR116", nameParts.secondName()); // Segundo nombre
        setCellValue(sheet, "R116", nameParts.secondName()); // Segundo nombre en R116

        // Identidad (formato numérico con comas)
        setCellValueNumeric(sheet, "C43", data.identity());
        setCellValueNumeric(sheet, "R119", data.identity());

        // Nacionalidad (mayúsculas)
        setCellValue(sheet, "AB43", data.nacion().toUpperCase());

        setCellValue(sheet, "D88", data.conduct().toUpperCase());
        setCellValue(sheet, "F90", data.radicado() != null ?
                (data.radicado() instanceof String ?
                        ((String) data.radicado()).toUpperCase() :
                        data.radicado().toString()) : "");
        setCellValue(sheet, "D92", data.fiscal().toUpperCase());
        setCellValue(sheet, "J92", data.juzgado().toUpperCase());
        String fullName = (data.names() + " " + data.lasname()).toUpperCase();
        setCellValue(sheet, "F98", fullName.trim());
        setCellValue(sheet, "A107", data.fact());
    }

    private void fillEstrategiaSheet(Sheet sheet, SolData data) {
        String fullName = (data.names() + " " + data.lasname()).toUpperCase();
        setCellValue(sheet, "C9", fullName.trim());
        setCellValue(sheet, "A20", data.fact());
        setCellValue(sheet, "E13", data.conduct().toUpperCase());
        setCellValue(sheet, "D15", data.radicado() != null ?
                (data.radicado() instanceof String ?
                        ((String) data.radicado()).toUpperCase() :
                        data.radicado().toString()) : "");

        setCellValue(sheet, "I15", data.fiscal().toUpperCase());
        setCellValue(sheet, "L15", data.juzgado().toUpperCase());
        setCellValue(sheet, "N17", data.date().toUpperCase());
        setCellValue(sheet, "A25", data.fact());
    }

    private void setCellValue(Sheet sheet, String cellReference, String value) {
        if (value == null) value = "";

        int rowNum = getRowFromReference(cellReference) - 1;
        int colNum = getColumnFromReference(cellReference);

        Row row = getOrCreateRow(sheet, rowNum);
        Cell cell = getOrCreateCell(row, colNum);

        cell.setCellValue(value);
    }

    private void setCellValueNumeric(Sheet sheet, String cellReference, Object value) {
        if (value == null) return;

        int rowNum = getRowFromReference(cellReference) - 1;
        int colNum = getColumnFromReference(cellReference);

        Row row = getOrCreateRow(sheet, rowNum);
        Cell cell = getOrCreateCell(row, colNum);

        if (value instanceof Number) {
            cell.setCellValue(((Number) value).doubleValue());
        } else {
            try {
                String strValue = value.toString().replaceAll("[^\\d]", "");
                if (!strValue.isEmpty()) {
                    double numericValue = Double.parseDouble(strValue);
                    cell.setCellValue(numericValue);
                    CellStyle style = sheet.getWorkbook().createCellStyle();
                    DataFormat format = sheet.getWorkbook().createDataFormat();
                    style.setDataFormat(format.getFormat("#,##0"));
                    cell.setCellStyle(style);
                }
            } catch (NumberFormatException e) {
                cell.setCellValue(value.toString());
            }
        }
    }

    private Row getOrCreateRow(Sheet sheet, int rowNum) {
        Row row = sheet.getRow(rowNum);
        if (row == null) {
            row = sheet.createRow(rowNum);
        }
        return row;
    }

    private Cell getOrCreateCell(Row row, int colNum) {
        Cell cell = row.getCell(colNum);
        if (cell == null) {
            cell = row.createCell(colNum);
        }
        return cell;
    }

    private int getRowFromReference(String cellReference) {
        String rowStr = cellReference.replaceAll("[A-Za-z]", "");
        return Integer.parseInt(rowStr);
    }

    private int getColumnFromReference(String cellReference) {
        String colStr = cellReference.replaceAll("[0-9]", "");

        int column = 0;
        for (int i = 0; i < colStr.length(); i++) {
            column = column * 26 + (colStr.charAt(i) - 'A' + 1);
        }
        return column - 1;
    }

    private NameParts splitNameParts(String names, String lastNames) {
        String[] nameParts = names.split(" ", 2);
        String[] lastNameParts = lastNames.split(" ", 2);

        String firstName = nameParts[0];
        String secondName = nameParts.length > 1 ? nameParts[1] : "";

        String firstLastName = lastNameParts[0];
        String secondLastName = lastNameParts.length > 1 ? lastNameParts[1] : "";

        return new NameParts(firstName, secondName, firstLastName, secondLastName);
    }

    private record NameParts(String firstName, String secondName, String firstLastName, String secondLastName) {}
}