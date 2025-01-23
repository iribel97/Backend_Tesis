package com.tesis.BackV2.controllers;

import com.tesis.BackV2.config.ApiResponse;
import com.tesis.BackV2.config.auth.AuthService;
import com.tesis.BackV2.dto.UsuarioDTO;
import com.tesis.BackV2.exceptions.MiExcepcion;
import com.tesis.BackV2.request.UsuarioRequest;
import com.tesis.BackV2.request.auth.UsuRequestPass;
import com.tesis.BackV2.services.UsuarioServ;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
@CrossOrigin(origins = {" http://10.16.141.29:4200"})
public class UsuController {
    @Autowired
    private UsuarioServ usuarioService;
    private final AuthService serviceA;

    @PutMapping()
    public ResponseEntity<ApiResponse<?>> actualizarUsuario(@RequestBody UsuarioRequest usuario) throws MiExcepcion {
        return ResponseEntity.ok(usuarioService.actualizarUser(usuario));
    }

    // Cambiar contraseña
    @PutMapping("cambiar/contrasena")
    public ResponseEntity<?> cambiarContrasena(@RequestBody UsuRequestPass request) {
        return ResponseEntity.ok(serviceA.cambiarPassUsuario(request.getContrasena(), request.getContrasenaNueva(), request.getCedula()));
    }


}
