package com.tesis.BackV2.dto.asistencia;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PorcentAsisEst {

    private int totalAsistencias;
    private int totalFaltas;
    private int totalJustificadas;
    private int totalAsist;

    private double porcentajeAsistencias;
    private double porcentajeFaltas;
    private double porcentajeJustificadas;
}
