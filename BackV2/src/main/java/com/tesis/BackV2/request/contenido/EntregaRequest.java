package com.tesis.BackV2.request.contenido;

import com.tesis.BackV2.request.documentation.DocumentoRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EntregaRequest {

    private long id;
    private boolean activo;
    private String contenido;

    private LocalDate fechaEntrega;
    private LocalTime horaEntrega;

    private long idAsignacion;
    private String idEstudiante;

    private List<DocumentoRequest> documentos;
}
