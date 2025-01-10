package com.tesis.BackV2.dto.notas;

import com.tesis.BackV2.entities.embedded.Calificacion;
import com.tesis.BackV2.enums.TipoNivel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SistCalfLevlCuatroDTO {

    private TipoNivel nivel;
    private Calificacion califID;
    private String descripcion;
    private String base;
    private String maximo;
}
