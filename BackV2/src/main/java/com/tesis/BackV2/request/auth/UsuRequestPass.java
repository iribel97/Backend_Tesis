package com.tesis.BackV2.request.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UsuRequestPass {

    private String cedula;
    private String contrasena;
    private String contrasenaNueva;
}
