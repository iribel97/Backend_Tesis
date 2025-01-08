package com.tesis.BackV2.dto.conducta;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DataConduct {

    private String primerParcial;
    private String segundoParcial;
    private String promedio;
}
