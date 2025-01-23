package com.tesis.BackV2.dto;

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
public class CitacionDTO {

    private long id;
    private LocalDate fecha;
    private LocalTime hora;
    private String motivo;
    private String observaciones;
    private String nombreRepresentante;
    private String nombreAlumno;

}
