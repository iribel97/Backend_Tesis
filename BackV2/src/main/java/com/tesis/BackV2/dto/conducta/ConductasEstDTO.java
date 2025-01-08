package com.tesis.BackV2.dto.conducta;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ConductasEstDTO {

    private String materia;

    private List<DataConduct> data;

    private String promedio;
}
