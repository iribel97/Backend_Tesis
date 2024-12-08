package com.tesis.BackV2.dto.doc;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DocumentoDTO {
    private long id;
    private String nombre;
    private String mime;
}
