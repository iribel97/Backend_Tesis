package com.tesis.BackV2.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DistributivoRequest {

    private long id;
    private long cicloId;
    private long aulaId;
    private long materiaId;
    private String cedulaDocente;
}
