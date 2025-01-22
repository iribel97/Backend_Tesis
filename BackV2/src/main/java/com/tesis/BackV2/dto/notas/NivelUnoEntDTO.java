package com.tesis.BackV2.dto.notas;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NivelUnoEntDTO {

    private String nombreNivel;
    private double promedio;
    private double sumativa;
    private double peso;

    List<NivelDosEntDTO> niveles;
}
