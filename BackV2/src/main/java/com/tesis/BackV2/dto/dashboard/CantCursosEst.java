package com.tesis.BackV2.dto.dashboard;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CantCursosEst {

    private String etiqueta;

    private List<CantEstCursoDTO> datos;

}
