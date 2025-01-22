package com.tesis.BackV2.dto.notas;

import com.tesis.BackV2.dto.contenido.NivelEntregaDTO;
import com.tesis.BackV2.entities.embedded.Calificacion;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NivelTresEntDTO {

    private Calificacion nivelAnt;
    private String nombreNivel;
    private double promedio;
    private double sumativa;
    private double peso;

    private List<NivelEntregaDTO> nivelEntregas;
}
