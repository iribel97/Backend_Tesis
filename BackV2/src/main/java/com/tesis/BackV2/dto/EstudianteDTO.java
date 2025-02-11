package com.tesis.BackV2.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EstudianteDTO {

    private long id;
    private LocalDate ingreso;
    private long idMatricula;
    private String curso;

    private RepresentanteDTO representante;
}
