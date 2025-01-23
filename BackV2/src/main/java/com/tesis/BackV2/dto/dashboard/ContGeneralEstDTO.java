package com.tesis.BackV2.dto.dashboard;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ContGeneralEstDTO {

    private String nombreEstudiante;
    private String curso;
    private double porcentajeAsist;
    private double promedio;
}
