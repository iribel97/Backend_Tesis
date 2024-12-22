package com.tesis.BackV2.request.documentation;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DocumentoRequest {

    private long id;
    private String nombre;
    private String base64;
    private String mime;

}
