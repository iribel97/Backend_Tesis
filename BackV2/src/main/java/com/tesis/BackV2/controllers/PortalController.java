package com.tesis.BackV2.controllers;

import com.tesis.BackV2.clases.LoginRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/")
@CrossOrigin(origins = "http://localhost:4200")
public class PortalController {
    @Autowired
    private AuthenticationManager authenticationManager;

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
}
