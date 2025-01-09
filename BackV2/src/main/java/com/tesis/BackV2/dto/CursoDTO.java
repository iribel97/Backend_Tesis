package com.tesis.BackV2.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CursoDTO {

    private long id;
    private String paralelo;
    private int maxEstudiantes;
    private int cant;
    private String nombreGrado;
    private String tutor;
    private String telefonoTutor;
    private String emailTutor;
}
