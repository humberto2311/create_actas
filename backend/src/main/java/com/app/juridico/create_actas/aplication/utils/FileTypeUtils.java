package com.app.juridico.create_actas.aplication.utils;

public class FileTypeUtils {
    
    private static final String DEFAULT_TYPE = "UNKNOWN";
    
    public static String getFileType(String fileName) {
        if (fileName == null || fileName.trim().isEmpty()) {
            return DEFAULT_TYPE;
        }
        
        String extension = extractExtension(fileName).toLowerCase();
        
        switch (extension) {
            case "pdf": return "PDF";
            case "doc": case "docx": case "rtf": case "odt": return "WORD";
            case "xls": case "xlsx": case "csv": case "ods": return "EXCEL";
            case "ppt": case "pptx": case "odp": return "POWERPOINT";
            case "jpg": case "jpeg": case "png": case "gif": case "bmp": return "IMAGE";
            case "txt": case "text": case "md": case "log": return "TEXT";
            case "zip": case "rar": case "7z": case "tar": return "ARCHIVE";
            case "mp3": case "wav": case "ogg": return "AUDIO";
            case "mp4": case "avi": case "mov": return "VIDEO";
            case "html": case "htm": return "WEB";
            default: return extension.toUpperCase();
        }
    }
    
    public static String extractExtension(String fileName) {
        if (fileName == null || fileName.trim().isEmpty()) {
            return "";
        }
        
        int lastDotIndex = fileName.lastIndexOf(".");
        if (lastDotIndex == -1 || lastDotIndex == fileName.length() - 1) {
            return "";
        }
        
        return fileName.substring(lastDotIndex + 1);
    }
}