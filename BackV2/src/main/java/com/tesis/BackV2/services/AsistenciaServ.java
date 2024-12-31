package com.tesis.BackV2.services;

import com.tesis.BackV2.config.ApiResponse;
import com.tesis.BackV2.repositories.AsistenciaRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AsistenciaServ {

    private final AsistenciaRepo repo;

    // registrar Asistencia
    public ApiResponse<String> registrarAsistencia() {
        return ApiResponse.<String> builder()
                .error(false)
                .codigo(200)
                .mensaje("Solicitud exitosa")
                .detalles("Asistencia registrada correctamente")
                .build();
    }
}
