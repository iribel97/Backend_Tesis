package com.tesis.BackV2.services.config;

import com.tesis.BackV2.config.ApiResponse;
import com.tesis.BackV2.entities.config.HorarioConfig;
import com.tesis.BackV2.exceptions.ApiException;
import com.tesis.BackV2.repositories.config.HorarioConfigRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HorarioConfigServ {

    @Autowired
    private HorarioConfigRepo horarioConfigRepo;

    // Crear
    public ApiResponse<?> crearHorarioConfig(HorarioConfig request) {
        if (validarHoras(request)) {
            throw new ApiException(ApiResponse.builder()
                    .error(true)
                    .mensaje("Solicitud inválida")
                    .codigo(400)
                    .detalles("El horario se cruza con otro horario.")
                    .build());
        }
        HorarioConfig horarioConfig = HorarioConfig.builder()
                .horaInicio(request.getHoraInicio())
                .horaFin(request.getHoraFin())
                .descripcion(request.getDescripcion())
                .build();
        horarioConfigRepo.save(horarioConfig);
        return ApiResponse.builder()
                .error(false)
                .mensaje("Horario creado")
                .codigo(200)
                .detalles("Horario creado con éxito.")
                .build();
    }

    // Listar todos
    public List<HorarioConfig> horarios(){
        return horarioConfigRepo.findAll();
    }

    // Editar
    public ApiResponse<?> editarHorarioConfig(HorarioConfig request) {
        HorarioConfig horarioConfig = horarioConfigRepo.findById(request.getId()).orElseThrow(() -> new ApiException(ApiResponse.builder()
                .error(true)
                .mensaje("Solicitud inválida")
                .codigo(404)
                .detalles("El horario no existe.")
                .build()));
        horarioConfig.setHoraInicio(request.getHoraInicio());
        horarioConfig.setHoraFin(request.getHoraFin());
        horarioConfig.setDescripcion(request.getDescripcion());
        horarioConfigRepo.save(horarioConfig);
        return ApiResponse.builder()
                .error(false)
                .mensaje("Horario editado")
                .codigo(200)
                .detalles("Horario editado con éxito.")
                .build();
    }

    // Eliminar
    public ApiResponse<?> eliminarHorarioConfig(long id) {
        HorarioConfig horarioConfig = horarioConfigRepo.findById(id).orElseThrow(() -> new ApiException(ApiResponse.builder()
                .error(true)
                .mensaje("Solicitud inválida")
                .codigo(404)
                .detalles("El horario no existe.")
                .build()));
        horarioConfigRepo.delete(horarioConfig);
        return ApiResponse.builder()
                .error(false)
                .mensaje("Horario eliminado")
                .codigo(200)
                .detalles("Horario eliminado con éxito.")
                .build();
    }

    private boolean validarHoras(HorarioConfig request) {
        List<HorarioConfig> horarios = horarioConfigRepo.findAll();

        for (HorarioConfig horario : horarios) {
            if (request.getHoraInicio().isBefore(horario.getHoraFin()) && request.getHoraFin().isAfter(horario.getHoraInicio())) {
                return true;
            }
        }
        return false;
    }
}
