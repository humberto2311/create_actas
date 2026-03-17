package com.app.juridico.create_actas.infrastructure.adapter.secondary.word;

import com.app.juridico.create_actas.infrastructure.adapter.secondary.ResourceLoaderAdapter;
import lombok.AllArgsConstructor;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.usermodel.Range;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

@Service
@AllArgsConstructor
public class WordSolicitudGeneratorAdapter {

    private final ResourceLoaderAdapter resourceLoaderAdapter;

    public GeneratedDocument generateSolicitudWord(SolicitudData data) {
        Map<String, String> dataMap = Map.of(
                "${NOMBRE_PERSONA}", data.name().toUpperCase() + " " + data.lastname().toUpperCase(),
                "${FECHA}", data.date().toString(),
                "${NUMERO_CC}", data.identity()
        );

        try (InputStream templateStream = resourceLoaderAdapter.loadWordTemplateStream();
             HWPFDocument document = new HWPFDocument(templateStream)) {

            Range range = document.getRange();
            for (Map.Entry<String, String> entry : dataMap.entrySet()) {
                range.replaceText(entry.getKey(), entry.getValue());
            }

            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            document.write(bos);

            String filename = String.format("ACTA DE DERECHOS Y OBLIGACIONES DEL CAPTURADO %s %s.doc",
                    data.name().replaceAll("\\s+", " "),
                    data.lastname().replaceAll("\\s+", " ")).toUpperCase();

            return new GeneratedDocument(bos.toByteArray(), filename);

        } catch (IOException e) {
            throw new RuntimeException("Failed to generate Word Solicitud document.", e);
        }
    }
}