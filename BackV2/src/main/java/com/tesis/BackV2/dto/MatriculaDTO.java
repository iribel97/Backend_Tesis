package com.tesis.BackV2.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MatriculaDTO {
    private Long id;
    private String cedulaEstudiante;
    private String nombreEstudiante;
    private String grado;
    private String estado;
    private String ciclo;
    private String fechaMatricula;
}
