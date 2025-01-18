package com.tesis.BackV2.dto.dashboard;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CantidadesDTO {

    private double porcentajeCompleto;
    private double porcentajeIncompleto;
    private int total;
    private int completo;
    private int incompleto;
}
