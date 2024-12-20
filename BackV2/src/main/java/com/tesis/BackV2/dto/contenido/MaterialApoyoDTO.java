package com.tesis.BackV2.dto.contenido;

import com.tesis.BackV2.dto.doc.DocumentoDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MaterialApoyoDTO {

    private long id;
    private boolean activo;
    private String link;
    private String nombreLink;
    private DocumentoDTO documento;
}
