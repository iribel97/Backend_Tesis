package com.tesis.BackV2.request.contenido;

import com.tesis.BackV2.request.documentation.DocumentoRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MaterialApoyoRequest {

    private boolean activo;
    private String link;
    private long idTema;
    private DocumentoRequest documento;
}
