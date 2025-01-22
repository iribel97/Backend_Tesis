package com.tesis.BackV2.dto;

import com.tesis.BackV2.dto.contenido.DisNotasEst;
import com.tesis.BackV2.dto.dashboard.CantAsisTutorDTO;
import com.tesis.BackV2.dto.dashboard.CantidadesDTO;
import com.tesis.BackV2.dto.dashboard.EstMasInasDTO;
import com.tesis.BackV2.dto.dashboard.EstudianteFaltasDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CursoDocenteTutorDTO {

    private String curso;

    private CantidadesDTO asistenciasGeneral;

    private List<CantAsisTutorDTO> asistenciaByMateria;

    private EstudianteFaltasDTO estMasInasistencias;

    private DisNotasEst peorProm;

    private List<DisNotasEst> notasEstudiantes;
}
