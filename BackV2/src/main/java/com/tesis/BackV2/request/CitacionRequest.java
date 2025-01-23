package com.tesis.BackV2.request;

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
public class CitacionRequest {

    private long id;

    private LocalDate fecha; // Fecha de la citación
    private LocalTime hora; // Hora de la citación
    private String motivo; // Razón o motivo de la citación
    private String observaciones; // Observaciones adicionales del docente

    private String cedulaEst;

}
