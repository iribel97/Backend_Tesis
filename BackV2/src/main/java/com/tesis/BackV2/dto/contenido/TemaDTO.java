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
public class TemaDTO {

        private long idTema;
        private boolean activo;
        private String nombreTema;
        private String descripcion;
        private List<MaterialApoyoDTO> materiales;
        private List<AsignacionesDTO> asignaciones;
}
