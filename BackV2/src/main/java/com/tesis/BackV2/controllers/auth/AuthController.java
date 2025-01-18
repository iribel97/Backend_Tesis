package com.tesis.BackV2.controllers.auth;

import com.tesis.BackV2.config.ApiResponse;
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
@CrossOrigin(origins = {"http://192.168.2.149:4200"})
public class AuthController {

    private final AuthService authService;

    //Inicio de sesión
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest loginRequest) {
        return ResponseEntity.ok(authService.login(loginRequest));
    }

    @PostMapping("/registro/adminA")
    public ResponseEntity<ApiResponse<?>> registerAdminA(@RequestBody RegisterRequest registerRequest) {
        return ResponseEntity.ok(authService.register(registerRequest, Rol.ADMIN, EstadoUsu.Activo));
    }

    //Registro de usuario representante
    @PostMapping("/registro")
    public ResponseEntity<ApiResponse<?>> register(@RequestBody RegisterRequest registerRequest) {
        return ResponseEntity.ok(authService.register(registerRequest, Rol.REPRESENTANTE, EstadoUsu.Activo));
    }


}
