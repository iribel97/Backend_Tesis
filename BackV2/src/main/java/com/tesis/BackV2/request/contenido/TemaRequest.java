package com.tesis.BackV2.request.contenido;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TemaRequest {

    private long id;
    private boolean activo;
    private String tema;
    private String detalle;
    private long idUnidad;
}
