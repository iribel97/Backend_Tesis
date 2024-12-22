package com.tesis.BackV2.dto;

import com.tesis.BackV2.entities.embedded.Calificacion;
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
public class SistCalfDTO {

    private TipoNivel nivel;
    private Calificacion califID;
    private String descripcion;
    private String peso;
    private TipoSistCalif tipo;
    private String base;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
}
