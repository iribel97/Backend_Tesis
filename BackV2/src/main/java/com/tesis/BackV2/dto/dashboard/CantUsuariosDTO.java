package com.tesis.BackV2.dto.dashboard;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CantUsuariosDTO {

    private int cantAdmin;
    private int cantAdminOp;
    private int cantDocente;
    private int cantEstudiante;
    private int cantRepresentante;
}
