package com.tesis.BackV2.services.cicloacademico;

import com.tesis.BackV2.config.ApiResponse;
import com.tesis.BackV2.entities.Grado;
import com.tesis.BackV2.exceptions.ApiException;
import com.tesis.BackV2.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class GradoServ {
    @Autowired
    private GradoRepo gradoRepo;
    @Autowired
    private CursoRepo cursoRepo;
    @Autowired
    private MateriaRepo materiaRepo;


    // Creación ejem: octavo, noveno, decimo
    @Transactional
    public ApiResponse<String> crearGrado(Grado request) {
        boolean gradoExiste = gradoRepo.existsByNombreIgnoreCase(request.getNombre());
        if (gradoExiste) {
            throw new ApiException(ApiResponse.builder()
                    .error(true)
                    .mensaje("Solicitud inválida")
                    .codigo(400)
                    .detalles("El grado académico ya existe.")
                    .build()
            );
        }

        Grado grado = Grado.builder()
                .nombre(request.getNombre())
                .build();

        gradoRepo.save(grado);
        return ApiResponse.<String>builder()
                .error(false)
                .mensaje("Grado académico creado")
                .codigo(200)
                .detalles("El grado académico ha sido creado correctamente.")
                .build();
    }

    // Traer todos
    public List<Grado> getGrados() { return gradoRepo.findAll(); }

    // Traer por nombre
    public Grado getGrado(String nombre) {
        Grado grado = gradoRepo.findByNombre(nombre);
        if (grado == null) {
            throw new ApiException(ApiResponse.builder()
                    .error(true)
                    .mensaje("Solicitud inválida")
                    .codigo(400)
                    .detalles("El grado académico no existe.")
                    .build()
            );
        }
        return gradoRepo.findByNombre(nombre);
    }

    // Acualizar
    @Transactional
    public ApiResponse<String> editarGrado(Grado request) {
        Grado grado = gradoRepo.findById(request.getId())
                .orElseThrow(() -> new ApiException(ApiResponse.builder()
                        .error(true)
                        .mensaje("Solicitud inválida")
                        .codigo(400)
                        .detalles("El grado académico no existe.")
                        .build()
                ));

        boolean nombreEnUso = gradoRepo.existsByNombreIgnoreCaseAndIdNot(request.getNombre(), request.getId());
        if (nombreEnUso) {
            throw new ApiException(ApiResponse.builder()
                    .error(true)
                    .mensaje("Solicitud inválida")
                    .codigo(400)
                    .detalles("El nombre del grado académico ya está en uso.")
                    .build()
            );
        }

        grado.setNombre(request.getNombre());
        gradoRepo.save(grado);

        return ApiResponse.<String>builder()
                .error(false)
                .mensaje("Grado académico actualizado")
                .codigo(200)
                .detalles("El grado académico ha sido actualizado correctamente.")
                .build();
    }

    // Eliminar
    @Transactional
    public ApiResponse<String> eliminarGrado(Long id) {
        Grado grado = gradoRepo.findById(id).orElseThrow(() -> new ApiException(ApiResponse.builder()
                .error(true)
                .mensaje("Solicitud inválida")
                .codigo(400)
                .detalles("El grado académico no existe.")
                .build()
        ));

        if (cursoRepo.existsByGradoId(id) || materiaRepo.existsByGradoId(id)) {
            throw new ApiException(ApiResponse.builder()
                    .error(true)
                    .mensaje("Solicitud inválida")
                    .codigo(400)
                    .detalles("El grado académico está asociado a un aula o materia.")
                    .build()
            );
        }

        gradoRepo.delete(grado);
        return ApiResponse.<String>builder()
                .error(false)
                .mensaje("Grado académico eliminado")
                .codigo(200)
                .detalles("El grado académico ha sido eliminado correctamente.")
                .build();
    }
}
