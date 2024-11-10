package com.tesis.BackV2.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CicloARequest {

    private String nombre;
    private String cedulaUsuario;
    private int cantPeriodos;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
}
