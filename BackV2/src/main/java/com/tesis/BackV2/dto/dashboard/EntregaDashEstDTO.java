package com.tesis.BackV2.dto.dashboard;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EntregaDashEstDTO {

    private long idAsig;
    private long idDis;
    private String nombreAsig;
    private String nombreMateria;
    private LocalDate fechaFin;
}
