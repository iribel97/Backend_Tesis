package com.tesis.BackV2.dto.asistencia;

import com.tesis.BackV2.enums.EstadoAsistencia;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AsisDocDTO {

    private long idAsistencia;;
    private String cedulaEstudiante;
    private Long idEstudiante;
    private String apellidosEstudiante;
    private String nombresEstudiante;
    private EstadoAsistencia estado;
    private String observaciones;
}
