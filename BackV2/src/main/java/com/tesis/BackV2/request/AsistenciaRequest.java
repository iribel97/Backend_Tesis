package com.tesis.BackV2.request;

import com.tesis.BackV2.enums.EstadoAsistencia;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AsistenciaRequest {

        private Long id;
        private EstadoAsistencia estado;
        private LocalDate fecha;
        private long horarioID;
        private String cedulaEstudiante;
}
