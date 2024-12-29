package com.tesis.BackV2.dto.contenido;

import com.tesis.BackV2.dto.horarioConfig.AgendaDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ContenidoDTO {

    private long idDistributivo;
    private String nombreMateria;
    private String docenteNombres;
    private String docenteApellidos;
    private List<AgendaDTO> horarios;
    private List<UnidadesDTO> unidades;

}
