package com.app.juridico.create_actas.aplication.utils;

import org.apache.pdfbox.pdmodel.PDDocument;


import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.extractor.WordExtractor;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

public class DocumentPageCounter {

    private static final int DEFAULT_PDF_ESTIMATED_LINES_PER_PAGE = 60;
    private static final int DEFAULT_DOC_LINES_PER_PAGE = 50;
    private static final int DEFAULT_EXCEL_ROWS_PER_PAGE = 40;

    public static int countPages(MultipartFile file) throws IOException {
        String fileName = file.getOriginalFilename();
        if (fileName == null) return 1;

        String extension = getFileExtension(fileName);

        try (InputStream is = file.getInputStream()) {
            switch (extension) {
                case "pdf":
                    return countPdfPages(is);
                case "docx":
                    return countDocxPages(is);
                case "doc":
                    return countDocPages(is);
                case "xlsx":
                case "xls":
                    return countExcelPages(is);
                case "jpg":
                case "jpeg":
                case "png":
                case "gif":
                case "bmp":
                    return 1; // Las imágenes siempre tienen 1 página
                case "txt":
                    return countTextPages(is, file.getSize());
                default:
                    return 1; // Otros tipos asumen 1 página
            }
        }
    }

    private static String getFileExtension(String fileName) {
        if (fileName == null) return "";
        int lastDotIndex = fileName.lastIndexOf(".");
        if (lastDotIndex == -1) return "";
        return fileName.substring(lastDotIndex + 1).toLowerCase();
    }

    private static int countPdfPages(InputStream is) throws IOException {
        try (PDDocument document = PDDocument.load(is)) {
            return document.getNumberOfPages();
        }
    }

    private static int countDocxPages(InputStream is) throws IOException {
        try (XWPFDocument document = new XWPFDocument(is)) {
            int pageCount = document.getProperties().getExtendedProperties().getUnderlyingProperties().getPages();
            if (pageCount <= 0) {
                pageCount = estimateDocxPages(document);
            }
            return Math.max(pageCount, 1);
        }
    }

    private static int estimateDocxPages(XWPFDocument document) {
        int totalLines = 0;
        for (XWPFParagraph paragraph : document.getParagraphs()) {
            String text = paragraph.getText();
            if (text != null && !text.trim().isEmpty()) {
                // Estimación simple: contar líneas basadas en saltos de línea
                totalLines += text.split("\n").length;
            }
        }
        return Math.max(1, (totalLines + DEFAULT_DOC_LINES_PER_PAGE - 1) / DEFAULT_DOC_LINES_PER_PAGE);
    }

    private static int countDocPages(InputStream is) throws IOException {
        try (HWPFDocument document = new HWPFDocument(is)) {
            WordExtractor extractor = new WordExtractor(document);
            String text = extractor.getText();
            int lineCount = text.split("\r\n|\r|\n").length;
            return Math.max(1, (lineCount + DEFAULT_DOC_LINES_PER_PAGE - 1) / DEFAULT_DOC_LINES_PER_PAGE);
        }
    }

    private static int countExcelPages(InputStream is) throws IOException {
        try (Workbook workbook = WorkbookFactory.create(is)) {
            int totalPages = 0;

            for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
                var sheet = workbook.getSheetAt(i);
                int rowCount = sheet.getPhysicalNumberOfRows();
                int sheetPages = Math.max(1, (rowCount + DEFAULT_EXCEL_ROWS_PER_PAGE - 1) / DEFAULT_EXCEL_ROWS_PER_PAGE);
                totalPages += sheetPages;
            }

            return Math.max(totalPages, 1);
        }
    }
    private static int countTextPages(InputStream is, long fileSize) throws IOException {
        String content = new String(is.readAllBytes());
        int lineCount = content.split("\r\n|\r|\n").length;
        return Math.max(1, (lineCount + DEFAULT_PDF_ESTIMATED_LINES_PER_PAGE - 1) / DEFAULT_PDF_ESTIMATED_LINES_PER_PAGE);
    }
}