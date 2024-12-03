package com.tesis.BackV2.request;

import com.tesis.BackV2.enums.EstadoUsu;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UsuarioEditRequest {

    private String cedula;
    private String estado;
}
