package com.tesis.BackV2.dto.asistencia;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AsistenciaEstDTO {
    private long id;
    private LocalDate fecha;
    private String diaSemana;
    private String estado;
    private String observaciones;
}
