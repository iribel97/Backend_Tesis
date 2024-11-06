package com.tesis.BackV2.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RepresentanteDTO {

    private Long id;
    private boolean autorizado;
    private String ocupacion;
    private String empresa;
    private String direccion;
    private String telefono;
}
