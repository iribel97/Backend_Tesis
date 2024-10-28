package com.tesis.BackV2.controllers;

import com.tesis.BackV2.clases.LoginRequest;
import com.tesis.BackV2.clases.RegistroDocenteDTO;
import com.tesis.BackV2.entities.Docente;
import com.tesis.BackV2.entities.Usuario;
import com.tesis.BackV2.exceptions.MiExcepcion;
import com.tesis.BackV2.services.CustomUserDetailsService;
import com.tesis.BackV2.services.UsuarioServ;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/usuario")
public class UsuarionCont {
    @Autowired
    private UsuarioServ usuarioService;

    @PostMapping("/registrar/Docente")
    public Docente registrarUsuario(@RequestBody RegistroDocenteDTO registroDocenteDTO) throws MiExcepcion {
        Usuario usuario = registroDocenteDTO.getUsuario();
        Docente docente = registroDocenteDTO.getDocente();

        usuarioService.crearUsuarioDocente(usuario, docente);
        return usuarioService.listDocId(usuario.getCedula());
    }
}
