package com.tesis.BackV2.services.contenido;

import com.tesis.BackV2.config.ApiResponse;
import com.tesis.BackV2.exceptions.ApiException;
import com.tesis.BackV2.repositories.contenido.UnidadRepo;
import com.tesis.BackV2.request.contenido.UnidadRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UnidadServ {

    private final UnidadRepo repo;

    // Crear una unidad
    public ApiResponse<String> crearUnidad(UnidadRequest request){
        validarRequest(request);

        return ApiResponse.<String>builder()
                .error(false)
                .codigo(200)
                .mensaje("Creacion unidad")
                .detalles("Correcta creación de la unidad")
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

        if (request.getIdMateria() == 0) {
            throw new ApiException(ApiResponse.builder()
                    .error(true)
                    .mensaje("Solicitud incorrecta")
                    .codigo(400)
                    .detalles("Id Materia es requerido")
                    .build()
            );
        }
    }
}
