package com.tesis.BackV2.services.cicloacademico;

import com.tesis.BackV2.config.ApiResponse;
import com.tesis.BackV2.entities.CicloAcademico;
import com.tesis.BackV2.exceptions.ApiException;
import com.tesis.BackV2.repositories.*;
import com.tesis.BackV2.request.CicloARequest;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CicloServ {
    @Autowired
    private CicloAcademicoRepo cicloRepo;
    @Autowired
    private DistributivoRepo distributivoRepo;

    // Creación
    @Transactional
    public ApiResponse<String> crearCicloAcademico(CicloARequest request) {
        boolean existeConflicto = cicloRepo.findAll().stream()
                .anyMatch(ciclo ->
                        request.getFechaInicio().isBefore(ciclo.getFechaFin()) &&
                                request.getFechaFin().isAfter(ciclo.getFechaInicio())
                );

        if (existeConflicto) {
            throw new ApiException(ApiResponse.builder()
                    .error(true)
                    .mensaje("Conflicto de fechas")
                    .codigo(400)
                    .detalles("El ciclo académico tiene conflicto de fechas con otro ciclo académico.")
                    .build()
            );
        }

        CicloAcademico ciclo = CicloAcademico.builder()
                .nombre(request.getNombre())
                .cantPeriodos(request.getCantPeriodos())
                .fechaInicio(request.getFechaInicio())
                .fechaFin(request.getFechaFin())
                .build();

        cicloRepo.save(ciclo);
        return ApiResponse.<String>builder()
                .error(false)
                .mensaje("Ciclo académico creado")
                .codigo(200)
                .detalles("El ciclo académico ha sido creado correctamente.")
                .build();
    }

    // Traer todos
    public List<CicloAcademico> getCiclos() {
        return cicloRepo.findAll();
    }

    // Traer un solo por id
    public CicloAcademico getCiclo(Long id) {
        return cicloRepo.findById(id).orElseThrow(() -> new RuntimeException("Ciclo académico no encontrado"));
    }

    // Editar
    @Transactional
    public ApiResponse<String> editarCiclo(CicloARequest request) {
        CicloAcademico ciclo = cicloRepo.findById(request.getId())
                .orElseThrow(() -> new ApiException(ApiResponse.builder()
                        .error(true)
                        .mensaje("Solicitud incorrecta")
                        .codigo(400)
                        .detalles("El ciclo académico no existe.")
                        .build()
                ));

        ciclo.setNombre(request.getNombre());
        ciclo.setCantPeriodos(request.getCantPeriodos());
        ciclo.setFechaInicio(request.getFechaInicio());
        ciclo.setFechaFin(request.getFechaFin());

        cicloRepo.save(ciclo);
        return ApiResponse.<String>builder()
                .error(false)
                .mensaje("Ciclo académico actualizado")
                .codigo(200)
                .detalles("El ciclo académico ha sido actualizado correctamente.")
                .build();
    }

    // Eliminar
    @Transactional
    public ApiResponse<String> eliminarCiclo(Long id) {
        CicloAcademico ciclo = cicloRepo.findById(id).orElseThrow(() -> new RuntimeException("Ciclo académico no encontrado."));

        if (distributivoRepo.existsByCicloId(id)) {
            throw new ApiException(ApiResponse.builder()
                    .error(true)
                    .mensaje("Solicitud incorrecta")
                    .codigo(400)
                    .detalles("No es posible eliminar el ciclo académico, tiene distributivos asociados.")
                    .build()
            );
        }

        cicloRepo.delete(ciclo);
        return ApiResponse.<String>builder()
                .error(false)
                .mensaje("Ciclo académico eliminado")
                .codigo(200)
                .detalles("El ciclo académico ha sido eliminado correctamente.")
                .build();
    }
}
