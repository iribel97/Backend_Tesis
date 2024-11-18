package com.tesis.BackV2.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DistributivoDTO {

    private long id;
    private String cicloAcademico;
    private String aula;
    private String grado;
    private String materia;
    private int horasSemanales;
    private int horasAsignadas;
    private String docente;
}
