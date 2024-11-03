package com.tesis.BackV2.controllers.auth;

import com.tesis.BackV2.config.auth.AuthResponse;
import com.tesis.BackV2.config.auth.AuthService;
import com.tesis.BackV2.config.auth.LoginRequest;
import com.tesis.BackV2.config.auth.RegisterRequest;
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

    //Registro de usuario
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest registerRequest) {
        return ResponseEntity.ok(authService.register(registerRequest));
    }
}
