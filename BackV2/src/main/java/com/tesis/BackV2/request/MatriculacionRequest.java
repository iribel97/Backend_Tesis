package com.tesis.BackV2.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MatriculacionRequest {

    private long id;
    private String cedulaEstudiante;
    private String grado;
    private String paralelo;

}
