package com.tesis.BackV2.dto.contenido;

import com.tesis.BackV2.dto.notas.NivelUnoEntDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DisNotasEst {

    private String nombreMateria;
    private String nombreEstudiante;
    private double promedio;

    List<NivelUnoEntDTO> promedios;
}
