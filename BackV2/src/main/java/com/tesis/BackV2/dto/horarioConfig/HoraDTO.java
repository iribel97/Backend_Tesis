package com.tesis.BackV2.dto.horarioConfig;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class HoraDTO {

    private String horaInicio;
    private String horaFin;

}
