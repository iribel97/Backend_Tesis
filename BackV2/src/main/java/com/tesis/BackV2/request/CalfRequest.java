package com.tesis.BackV2.request;

import com.tesis.BackV2.enums.TipoNivel;
import com.tesis.BackV2.enums.TipoSistCalif;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CalfRequest {

    private TipoNivel nivel;
    private String descripcion;
    private String peso;
    private TipoSistCalif tipo;
    private String base;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
}
