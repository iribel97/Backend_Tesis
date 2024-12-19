package com.tesis.BackV2.services;

import com.tesis.BackV2.infra.CorreoManager;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@NoArgsConstructor
public class CorreoServ {
    @Autowired
    CorreoManager correoManager;

    public void enviarCorreo(String correo, String asunto, String mensaje) {
        correoManager.enviarCorreo(correo, asunto, mensaje);
    }

}
