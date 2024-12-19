package com.tesis.BackV2.services.contenido;

import com.tesis.BackV2.config.ApiResponse;
import com.tesis.BackV2.dto.contenido.UnidadDTO;
import com.tesis.BackV2.entities.contenido.Unidad;
import com.tesis.BackV2.exceptions.ApiException;
import com.tesis.BackV2.repositories.DistributivoRepo;
import com.tesis.BackV2.repositories.contenido.UnidadRepo;
import com.tesis.BackV2.request.contenido.UnidadRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class UnidadServ {

    private final UnidadRepo repo;
    private final DistributivoRepo repoDis;

    // Crear una unidad
    @Transactional
    public ApiResponse<String> crearUnidad(UnidadRequest request){
        validarRequest(request);

        Unidad unidad = Unidad.builder()
                .titulo(request.getTema())
                .activo(request.isActivo())
                .distributivo(repoDis.findById(request.getIdDistributivo()).orElse(null))
                .build();

        repo.save(unidad);

        return ApiResponse.<String>builder()
                .error(false)
                .codigo(200)
                .mensaje("Creacion unidad")
                .detalles("Correcta creación de la unidad")
                .build();
    }

    // Traer una unidad
    public UnidadDTO obtenerUnidad(long id){
        // Validar que exista la unidad
        Unidad unidad = repo.findById(id).orElse(null);

        if (unidad == null){
            throw new ApiException(ApiResponse.builder()
                    .error(true)
                    .mensaje("Unidad no encontrada")
                    .codigo(404)
                    .detalles("La unidad no existe")
                    .build()
            );
        }
        return convertirADTO(unidad);
    }

    // Traer todas las unidades del distributivo
    public List<UnidadDTO> obtenerUnidades(long idDistributivo){
        return repo.findByDistributivo_Id(idDistributivo).stream()
                .map(this::convertirADTO)
                .toList();
    }

    // Traer unidad por distributivo y activo
    public List<UnidadDTO> obtenerUnidadesActivas(long idDistributivo){
        return repo.findByDistributivo_IdAndActivo(idDistributivo, true).stream()
                .map(this::convertirADTO)
                .toList();
    }

    // Traer unidad activa
    public UnidadDTO obtenerUnidadActiva(long id){
        // Validar que exista la unidad
        Unidad unidad = repo.findById(id).orElse(null);

        if (unidad == null || !unidad.isActivo()){
            throw new ApiException(ApiResponse.builder()
                    .error(true)
                    .mensaje("Unidad no encontrada")
                    .codigo(404)
                    .detalles("La unidad no existe o no está activa")
                    .build()
            );
        }
        return convertirADTO(unidad);
    }

    // Editar
    @Transactional
    public ApiResponse<String> editarUnidad(UnidadRequest request){
        validarRequest(request);

        Unidad unidad = repo.findById(request.getId()).orElse(null);

        if (unidad == null){
            throw new ApiException(ApiResponse.builder()
                    .error(true)
                    .mensaje("Unidad no encontrada")
                    .codigo(404)
                    .detalles("La unidad no existe")
                    .build()
            );
        }

        unidad.setTitulo(request.getTema());
        unidad.setActivo(request.isActivo());
        unidad.setDistributivo(repoDis.findById(request.getIdDistributivo()).orElse(null));

        repo.save(unidad);

        return ApiResponse.<String>builder()
                .error(false)
                .codigo(200)
                .mensaje("Edición unidad")
                .detalles("Correcta edición de la unidad")
                .build();
    }

    // Eliminar
    @Transactional
    public ApiResponse<String> eliminarUnidad(long id){
        Unidad unidad = repo.findById(id).orElse(null);

        if (unidad == null){
            throw new ApiException(ApiResponse.builder()
                    .error(true)
                    .mensaje("Unidad no encontrada")
                    .codigo(404)
                    .detalles("La unidad no existe")
                    .build()
            );
        }

        repo.delete(unidad);

        return ApiResponse.<String>builder()
                .error(false)
                .codigo(200)
                .mensaje("Eliminación unidad")
                .detalles("Correcta eliminación de la unidad")
                .build();
    }

    /* ----- METODOS PROPIOS DEL SREVICIO ----- */
    private void validarRequest(UnidadRequest request){
        if (request.getTema().isEmpty()){
            throw new ApiException(ApiResponse.builder()
                    .error(true)
                    .mensaje("Solicitud incorrecta")
                    .codigo(400)
                    .detalles("Tema es requerido")
                    .build()
            );
        }

        if (request.getIdDistributivo() == 0) {
            throw new ApiException(ApiResponse.builder()
                    .error(true)
                    .mensaje("Solicitud incorrecta")
                    .codigo(400)
                    .detalles("Id Materia es requerido")
                    .build()
            );
        }

        if (repoDis.findById(request.getIdDistributivo()).isEmpty()){
            throw new ApiException(ApiResponse.builder()
                    .error(true)
                    .mensaje("Solicitud incorrecta")
                    .codigo(400)
                    .detalles("Materia no encontrada")
                    .build()
            );
        }
    }

    private UnidadDTO convertirADTO(Unidad unidad){
        return UnidadDTO.builder()
                .id(unidad.getId())
                .tema(unidad.getTitulo())
                .activo(unidad.isActivo())
                .build();
    }
}
