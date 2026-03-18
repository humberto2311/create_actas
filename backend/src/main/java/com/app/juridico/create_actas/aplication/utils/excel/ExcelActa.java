package com.app.juridico.create_actas.aplication.utils.excel;

import com.app.juridico.create_actas.domain.entities.DocumentProcess;
import com.app.juridico.create_actas.infrastructure.adapter.secondary.excel.ActaData;
import com.app.juridico.create_actas.infrastructure.adapter.secondary.excel.ExcelActaGeneratorAdapter;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class ExcelActa {

    private final ExcelActaGeneratorAdapter excelActaGeneratorAdapter;

    public byte[] generateActaFromDocumentProcess(DocumentProcess documentProcess) {
        ActaData actaData = new ActaData(
                documentProcess.getNames() + " " + documentProcess.getLastNames(),
                documentProcess.getDate().toString(),
                documentProcess.getConduct(),
                documentProcess.getJuzgado(),
                documentProcess.getRadicado(),
                documentProcess.isState()
        );

        return excelActaGeneratorAdapter.generateActaExcel(actaData);
    }
}