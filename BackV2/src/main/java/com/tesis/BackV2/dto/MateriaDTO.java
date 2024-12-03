package com.tesis.BackV2.dto;

import com.tesis.BackV2.request.CalfRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MateriaDTO {

    private long id;
    private String nombre;
    private String area;
    private int horasSemanales;
    private String nombreGrado;
    private List<CalfRequest> sistemaCalificacion;

}
