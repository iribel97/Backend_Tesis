package com.tesis.BackV2.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class HorarioDTO {

    private Long id;
    private String diaSemana;
    private String horaInicio;
    private String horaFin;

    private String ciclo;
    private String curso;
    private String materia;
    private String docente;

}
