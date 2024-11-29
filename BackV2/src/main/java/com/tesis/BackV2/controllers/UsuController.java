package com.tesis.BackV2.controllers;

import com.tesis.BackV2.config.ApiResponse;
import com.tesis.BackV2.dto.UsuarioDTO;
import com.tesis.BackV2.exceptions.MiExcepcion;
import com.tesis.BackV2.request.UsuarioRequest;
import com.tesis.BackV2.services.UsuarioServ;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://localhost:4200"})
public class UsuController {
    @Autowired
    private UsuarioServ usuarioService;

    @GetMapping("{cedula}")
    public ResponseEntity<UsuarioDTO> buscarUsuario(@PathVariable String cedula) throws MiExcepcion {
        return ResponseEntity.ok(usuarioService.buscarUsuario(cedula));
    }

    @PutMapping()
    public ResponseEntity<ApiResponse<?>> actualizarUsuario(@RequestBody UsuarioRequest usuario) throws MiExcepcion {
        return ResponseEntity.ok(usuarioService.actualizarUser(usuario));
    }


}
