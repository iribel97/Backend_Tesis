package com.tesis.BackV2.dto.asistencia;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AsistenciasDisEstDTO {

    private String nombreMateria;

    private PorcentAsisEst data;
}
