package com.tesis.BackV2.request.contenido;

import com.tesis.BackV2.entities.embedded.Calificacion;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AsignacionRequest {

    private long id;

    private boolean activo;
    private String nombre;
    private String descripcion;
    private LocalDate fechaInicio;
    private LocalTime horaInicio;
    private LocalDate fechaFin;
    private LocalTime horaFin;

    private long idTema;

    private Calificacion calif;

}
