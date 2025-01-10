package com.tesis.BackV2.dto.notas;

import com.tesis.BackV2.entities.embedded.Calificacion;
import com.tesis.BackV2.enums.TipoNivel;
import com.tesis.BackV2.enums.TipoSistCalif;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SistCalfLevlTresDTO {

    private TipoNivel nivel;
    private Calificacion califID;
    private String descripcion;
    private String peso;
    private TipoSistCalif tipo;
    private String base;
    private String maximo;

    private List<SistCalfLevlCuatroDTO> level4;
}
