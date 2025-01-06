package com.tesis.BackV2.dto.cicloAcademico;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CicloDTO {

    private long id;

    private String nombre;
    private int cantPeriodos;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private boolean activo;

    private boolean requierePruebas;
}
