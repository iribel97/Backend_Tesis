package com.tesis.BackV2.dto.horarioConfig;

import com.tesis.BackV2.enums.DiaSemana;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AgendaDTO {

    private DiaSemana dia;
    private String horaInicio;
    private String horaFin;
}
