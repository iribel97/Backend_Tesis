package com.tesis.BackV2.dto.contenido;

import com.tesis.BackV2.enums.EstadoEntrega;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ContNivelEntregaDTO {

    private long id;
    private String nombreAsig;
    private double nota;
    private int notaMax;
    private EstadoEntrega estado;
}
