package com.app.juridico.create_actas.infrastructure.adapter.secondary.excel;

import com.app.juridico.create_actas.infrastructure.adapter.secondary.ResourceLoaderAdapter;
import lombok.AllArgsConstructor;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.Sheet;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;


/**
 * Adaptador para generar documentos Excel usando Apache POI, implementando
 * el puerto DocumentGeneratorPort (no mostrado aquí).
 */
@Service
@AllArgsConstructor
public class ExcelActaGeneratorAdapter {

    private final ResourceLoaderAdapter resourceLoaderAdapter;
    public byte[] generateActaExcel(ActaData data) {
        try (InputStream templateStream = resourceLoaderAdapter.loadExcelTemplateStream();
             Workbook workbook = resourceLoaderAdapter.createWorkbookFromTemplate()) {
            Sheet sheet = workbook.getSheetAt(0);
            sheet.getRow(2).getCell(1).setCellValue(data.clientName());
            sheet.getRow(3).getCell(1).setCellValue(data.date());
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            workbook.write(bos);
            return bos.toByteArray();
            
        } catch (IOException e) {
            throw new RuntimeException("Failed to generate Excel Acta document.", e);
        }
    }
}