package com.tesis.BackV2.request.contenido;

import com.tesis.BackV2.entities.embedded.Calificacion;
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
public class AsignacionRequest {

    private long id;

    private boolean visualizar;
    private String nombre;
    private String descripcion;
    private LocalDate fechaInicio;
    private LocalTime horaInicio;
    private LocalDate fechaFin;
    private LocalTime horaFin;

    private long idTema;

    private Calificacion calif;

    private List<DocumentoRequest> documentos;

}
