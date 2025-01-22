package com.tesis.BackV2.dto.dashboard;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CantAsisTutorDTO {

    private String materia;

    private int asistidos;
    private int faltas;
    private int justificados;
}
