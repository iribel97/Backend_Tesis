package com.tesis.BackV2.dto.contenido;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UnidadesDTO {
    private long idUnidad;
    private boolean activo;
    private String nombre;
    private List<TemaDTO> contenido;
}
