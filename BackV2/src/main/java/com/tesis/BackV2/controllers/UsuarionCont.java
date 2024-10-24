package com.tesis.BackV2.controllers;

import com.tesis.BackV2.clases.LoginRequest;
import com.tesis.BackV2.clases.RegistroDocenteDTO;
import com.tesis.BackV2.entities.Docente;
import com.tesis.BackV2.entities.Usuario;
import com.tesis.BackV2.exceptions.MiExcepcion;
import com.tesis.BackV2.services.CustomUserDetailsService;
import com.tesis.BackV2.services.UsuarioServ;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/usuario")
public class UsuarionCont {
    @Autowired
    private UsuarioServ usuarioService;

    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private CustomUserDetailsService userDetailsService;
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    // INICIAR SESIÓN
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        try {
            // Autenticación del usuario
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getCedula(), loginRequest.getPassword())
            );

            // Si llega aquí, la autenticación fue exitosa
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // Aquí podrías generar un JWT y devolverlo en la respuesta
            return ResponseEntity.ok("Login successful");

        } catch (BadCredentialsException e) {
            // Si las credenciales son incorrectas
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        }
    }

    @PostMapping("/registrar/Docente")
    public Docente registrarUsuario(@RequestBody RegistroDocenteDTO registroDocenteDTO) throws MiExcepcion {
        Usuario usuario = registroDocenteDTO.getUsuario();
        Docente docente = registroDocenteDTO.getDocente();

        usuarioService.crearUsuarioDocente(usuario, docente);
        return usuarioService.listDocId(usuario.getCedula());
    }
}
