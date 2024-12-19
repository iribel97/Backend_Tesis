package com.tesis.BackV2.request.contenido;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UnidadRequest {

    private long id;
    private String tema;
    private boolean activo;
    private long idDistributivo;
}
