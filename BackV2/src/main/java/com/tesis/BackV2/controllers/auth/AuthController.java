package com.tesis.BackV2.controllers.auth;

import com.tesis.BackV2.config.auth.AuthResponse;
import com.tesis.BackV2.config.auth.AuthService;
import com.tesis.BackV2.config.auth.LoginRequest;
import com.tesis.BackV2.config.auth.RegisterRequest;
import com.tesis.BackV2.enums.EstadoUsu;
import com.tesis.BackV2.enums.Rol;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://localhost:4200"})
public class AuthController {

    private final AuthService authService;

    //Inicio de sesión
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest loginRequest) {
        return ResponseEntity.ok(authService.login(loginRequest));
    }

    @PostMapping("/register/adminA")
    public ResponseEntity<AuthResponse> registerAdminA(@RequestBody RegisterRequest registerRequest) {
        return ResponseEntity.ok(authService.register(registerRequest, Rol.ADMIN, EstadoUsu.Activo));
    }

    //Registro de usuario administrados
    @PostMapping("/register/admin")
    public ResponseEntity<AuthResponse> registerAdmin(@RequestBody RegisterRequest registerRequest) {
        return ResponseEntity.ok(authService.register(registerRequest, Rol.ADMIN, EstadoUsu.Inactivo));
    }

    @PostMapping("/register/adminOp")
    public ResponseEntity<AuthResponse> registerAdminOp(@RequestBody RegisterRequest registerRequest) {
        return ResponseEntity.ok(authService.register(registerRequest, Rol.AOPERACIONAL, EstadoUsu.Inactivo));
    }

    // Registro de usuario docente
    @PostMapping("/register/docente")
    public ResponseEntity<AuthResponse> registerDocente(@RequestBody RegisterRequest registerRequest) {
        return ResponseEntity.ok(authService.register(registerRequest, Rol.DOCENTE, EstadoUsu.Inactivo));
    }

    //Registro de usuario representante
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest registerRequest) {
        return ResponseEntity.ok(authService.register(registerRequest, Rol.REPRESENTANTE, EstadoUsu.Activo));
    }
}
