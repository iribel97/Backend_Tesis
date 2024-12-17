package com.tesis.BackV2.services.contenido;

import com.tesis.BackV2.config.ApiResponse;
import com.tesis.BackV2.dto.contenido.TemaDTO;
import com.tesis.BackV2.entities.contenido.Tema;
import com.tesis.BackV2.entities.contenido.Unidad;
import com.tesis.BackV2.exceptions.ApiException;
import com.tesis.BackV2.repositories.contenido.TemaRepo;
import com.tesis.BackV2.repositories.contenido.UnidadRepo;
import com.tesis.BackV2.request.contenido.TemaRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TemaService {

    private final TemaRepo repo;
    private final UnidadRepo repoUni;

    // Crear un Tema
    @Transactional
    public ApiResponse<String> crearTema(TemaRequest request){

        validarRequest(request);

        Tema tema = Tema.builder()
                .activo(request.isActivo())
                .tema(request.getTema())
                .detalle(request.getDetalle())
                .unidad(repoUni.findById(request.getIdUnidad()).orElseThrow(() -> new ApiException(ApiResponse.builder()
                        .error(true)
                        .codigo(404)
                        .mensaje("Error de validación")
                        .detalles("La unidad no existe")
                        .build())))
                .build();

        repo.save(tema);
        return ApiResponse.<String>builder()
                .error(false)
                .codigo(200)
                .mensaje("Tema creado correctamente")
                .detalles("El tema se ha creado correctamente")
                .build();
    }

    // Actualizar un Tema
    @Transactional
    public ApiResponse<String> editarTema(TemaRequest request){

        validarRequest(request);

        Tema tema = repo.findById(request.getId()).orElseThrow(() -> new ApiException(ApiResponse.builder()
                .error(true)
                .codigo(404)
                .mensaje("Error de validación")
                .detalles("El tema no existe")
                .build()));

        tema.setActivo(request.isActivo());
        tema.setTema(request.getTema());
        tema.setDetalle(request.getDetalle());
        tema.setUnidad(repoUni.findById(request.getIdUnidad()).orElseThrow(() -> new ApiException(ApiResponse.builder()
                .error(true)
                .codigo(404)
                .mensaje("Error de validación")
                .detalles("La unidad no existe")
                .build())));

        repo.save(tema);
        return ApiResponse.<String>builder()
                .error(false)
                .codigo(200)
                .mensaje("Tema actualizado correctamente")
                .detalles("El tema se ha actualizado correctamente")
                .build();
    }

    // Eliminar un Tema
    @Transactional
    public ApiResponse<String> eliminarTema(long id){

        Tema tema = repo.findById(id).orElseThrow(() -> new ApiException(ApiResponse.builder()
                .error(true)
                .codigo(404)
                .mensaje("Error de validación")
                .detalles("El tema no existe")
                .build()));

        repo.delete(tema);
        return ApiResponse.<String>builder()
                .error(false)
                .codigo(200)
                .mensaje("Tema eliminado correctamente")
                .detalles("El tema se ha eliminado correctamente")
                .build();
    }

    // Listar temas por unidad
    public List<TemaDTO> obtenerTemas(long idUnidad){
        return repo.findByUnidadId(idUnidad).stream().map(this::convertirADTO).toList();
    }

    // Traer un tema
    public TemaDTO obtenerTema(long id){
        return convertirADTO(repo.findById(id).orElseThrow(() -> new ApiException(ApiResponse.builder()
                .error(true)
                .codigo(404)
                .mensaje("Error de validación")
                .detalles("El tema no existe")
                .build())));
    }

    // Traer temas activos por unidad
    public List<TemaDTO> obtenerTemasActivos(long idUnidad){
        return repo.findByUnidadIdAndActivo(idUnidad, true).stream().map(this::convertirADTO).toList();
    }

    // Traer un tema activo
    public TemaDTO obtenerTemaActivo(long id){
        return convertirADTO(repo.findByIdAndActivo(id, true));
    }


    /* ----------------- METODOS PROPIOS ----------------- */
    private void validarRequest(TemaRequest request){
        if (request.getTema() == null || request.getTema().isEmpty()){
            throw new ApiException(ApiResponse.builder()
                    .error(true)
                    .codigo(400)
                    .mensaje("Error de validación")
                    .detalles("El tema no puede estar vacío")
                    .build());
        }

        if (request.getDetalle() == null || request.getDetalle().isEmpty()){
            throw new ApiException(ApiResponse.builder()
                    .error(true)
                    .codigo(400)
                    .mensaje("Error de validación")
                    .detalles("El detalle no puede estar vacío")
                    .build());
        }

        if (request.getIdUnidad() == 0){
            throw new ApiException(ApiResponse.builder()
                    .error(true)
                    .codigo(400)
                    .mensaje("Error de validación")
                    .detalles("La unidad no puede estar vacía")
                    .build());
        }
    }

    private TemaDTO convertirADTO(Tema tema){
        return TemaDTO.builder()
                .id(tema.getId())
                .activo(tema.isActivo())
                .tema(tema.getTema())
                .detalle(tema.getDetalle())
                .build();
    }

}
