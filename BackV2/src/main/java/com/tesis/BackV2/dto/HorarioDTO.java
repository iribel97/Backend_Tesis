package com.tesis.BackV2.dto;

import com.tesis.BackV2.dto.horarioConfig.DiaDTO;
import com.tesis.BackV2.dto.horarioConfig.HoraDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class HorarioDTO {

    private Long id;
    private HoraDTO horario;
    private DiaDTO lunes;
    private DiaDTO martes;
    private DiaDTO miercoles;
    private DiaDTO jueves;
    private DiaDTO viernes;


}
