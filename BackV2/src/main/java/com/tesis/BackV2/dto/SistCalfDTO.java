package com.tesis.BackV2.dto;

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

    private String ciclo;
    private TipoNivel nivel;
    private String peso;
    private TipoSistCalif tipo;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
}
