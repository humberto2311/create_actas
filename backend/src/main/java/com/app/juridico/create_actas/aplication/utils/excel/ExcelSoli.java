package com.app.juridico.create_actas.aplication.utils.excel;

import com.app.juridico.create_actas.domain.entities.DocumentProcess;
import com.app.juridico.create_actas.infrastructure.adapter.secondary.excel.ActaData;
import com.app.juridico.create_actas.infrastructure.adapter.secondary.excel.ExcelActaGeneratorAdapter;
import com.app.juridico.create_actas.infrastructure.adapter.secondary.excel.ExcelSolicitudGeneratoAdapter;
import com.app.juridico.create_actas.infrastructure.adapter.secondary.excel.SolData;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class ExcelSoli {
    private final ExcelSolicitudGeneratoAdapter excelSolicitudGeneratoAdapter;

    public byte[] generateSolidFromDocumentProcess(DocumentProcess documentProcess) {
        SolData solData= new SolData(
                documentProcess.getNames() ,
                documentProcess.getLastNames(),
                documentProcess.getDate().toString(),
                documentProcess.getConduct(),
                documentProcess.getJuzgado(),
                documentProcess.getRadicado(),
                documentProcess.isState(),
                documentProcess.getIdentity(),
                documentProcess.getNacion(),
                documentProcess.getFiscal(),
                documentProcess.getFact(),
                documentProcess.getCaptura()
        );


        return excelSolicitudGeneratoAdapter.generateSoliExcel(solData);
    }
}
