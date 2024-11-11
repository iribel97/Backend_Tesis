package com.tesis.BackV2.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AulaRequest {

    private String paralelo;
    private int cantEstudiantes;
    private String grado;
    private String cedulaTutor;


}
