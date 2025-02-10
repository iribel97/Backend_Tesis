package com.tesis.BackV2.dto.asistencia;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AsistenciasEstudianteRepDTO {

    private String apellidos;
    private String nombres;
    private String curso;

    private PorcentAsisEst data;
}
