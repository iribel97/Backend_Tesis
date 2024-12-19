package com.tesis.BackV2.dto.contenido;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UnidadDTO {

    private long id;
    private String tema;
    private boolean activo;
}
