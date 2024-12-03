package com.tesis.BackV2.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SisCalfRequest {

    private long cicloID;
    private long registro;
    private List<CalfRequest> sistemaCalificacion;

}
