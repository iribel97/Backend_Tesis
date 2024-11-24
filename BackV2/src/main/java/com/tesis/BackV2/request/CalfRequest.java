package com.tesis.BackV2.request;

import com.tesis.BackV2.enums.TipoNivel;
import com.tesis.BackV2.enums.TipoSistCalif;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CalfRequest {

    private TipoNivel nivel;
    private String descripcion;
    private String peso;
    private TipoSistCalif tipo;
}
