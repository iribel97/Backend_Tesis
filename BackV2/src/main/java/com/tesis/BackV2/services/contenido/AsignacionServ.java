package com.tesis.BackV2.services.contenido;

import com.tesis.BackV2.config.ApiResponse;
import com.tesis.BackV2.entities.SistemaCalificacion;
import com.tesis.BackV2.entities.contenido.Asignacion;
import com.tesis.BackV2.entities.contenido.Tema;
import com.tesis.BackV2.entities.documentation.DocContMateria;
import com.tesis.BackV2.exceptions.ApiException;
import com.tesis.BackV2.repositories.SistCalifRepo;
import com.tesis.BackV2.repositories.contenido.AsignacionRepo;
import com.tesis.BackV2.repositories.contenido.TemaRepo;
import com.tesis.BackV2.repositories.documentation.DocMaterialApoyoRepo;
import com.tesis.BackV2.request.contenido.AsignacionRequest;
import com.tesis.BackV2.request.documentation.DocumentoRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AsignacionServ {

    private final AsignacionRepo repo;
    private final SistCalifRepo sistCalifRepo;
    private final TemaRepo temaRepo;
    private final DocMaterialApoyoRepo docMatRepo;


    // Crear asignación
    @Transactional
    public ApiResponse<String> crearAsignacion (AsignacionRequest request) {

        validarDatos(request);

        Tema tema = temaRepo.findById(request.getIdTema()).get();
        SistemaCalificacion calif = sistCalifRepo.findById(request.getCalif()).get();

        Asignacion asignacion = Asignacion.builder()
                .activo(request.isVisualizar())
                .nombre(request.getNombre())
                .descripcion(request.getDescripcion())
                .fechaInicio(request.getFechaInicio())
                .horaInicio(request.getHoraInicio())
                .fechaFin(request.getFechaFin())
                .horaFin(request.getHoraFin())
                .tema(tema)
                .calif(calif)
                .build();

        Asignacion asignacionGuardada = repo.save(asignacion);

        // Guardar los documentos asociados
        if (request.getDocumentos() != null && !request.getDocumentos().isEmpty()) {
            List<DocContMateria> documentos = request.getDocumentos().stream()
                    .map(doc -> {
                        DocContMateria documento = DocContMateria.builder()
                                .nombre(doc.getNombre())
                                .mime(doc.getMime())
                                .tipoDoc("Asignación")
                                .contenido(Base64.getDecoder().decode(doc.getBase64()))
                                .asignacion(asignacionGuardada)
                                .build();
                        return docMatRepo.save(documento);
                    })
                    .toList();

        }

        return ApiResponse.<String>builder()
                .error(false)
                .codigo(200)
                .mensaje("Asignación creada")
                .detalles("La asignación ha sido creada exitosamente")
                .build();
    }

    // Editar asignación
    @Transactional
    public ApiResponse<String> editarAsignacion(AsignacionRequest request) {
        validarDatos(request);

        Asignacion asignacion = repo.findById(request.getId()).orElseThrow(() -> new ApiException(ApiResponse.<String>builder()
                .error(true)
                .codigo(400)
                .mensaje("Solicitud inválida")
                .detalles("La asignación con id " + request.getId() + " no ha sido encontrada")
                .build()));

        asignacion.setActivo(request.isVisualizar());
        asignacion.setNombre(request.getNombre());
        asignacion.setDescripcion(request.getDescripcion());
        asignacion.setFechaInicio(request.getFechaInicio());
        asignacion.setHoraInicio(request.getHoraInicio());
        asignacion.setFechaFin(request.getFechaFin());
        asignacion.setHoraFin(request.getHoraFin());

        Asignacion asignacionGuardada = repo.save(asignacion);

        // Guardar los documentos asociados
        if (request.getDocumentos() != null && !request.getDocumentos().isEmpty()) {
            List<DocContMateria> documentos = request.getDocumentos().stream()
                    .map(doc -> {
                        DocContMateria documento = DocContMateria.builder()
                                .nombre(doc.getNombre())
                                .mime(doc.getMime())
                                .tipoDoc("Asignación")
                                .contenido(Base64.getDecoder().decode(doc.getBase64()))
                                .asignacion(asignacionGuardada)
                                .build();
                        return docMatRepo.save(documento);
                    })
                    .toList();

        }

        return ApiResponse.<String>builder()
                .error(false)
                .codigo(200)
                .mensaje("Asignación editada")
                .detalles("La asignación ha sido editada exitosamente")
                .build();
    }

    /* ---- METODOS PROPIOS DEL SERVICIO ---- */
    private void validarDatos(AsignacionRequest request) {
        // Traer el tema
        temaRepo.findById(request.getIdTema()).orElseThrow(() -> new ApiException(ApiResponse.<String>builder()
                .error(true)
                .codigo(400)
                .mensaje("Solicitud inválida")
                .detalles("El tema con id " + request.getIdTema() + " no ha sido encontrado")
                .build()));

        // validar el id del sistema de calificación
        sistCalifRepo.findById(request.getCalif()).orElseThrow( () -> new ApiException(ApiResponse.<String>builder()
                .error(true)
                .codigo(400)
                .mensaje("Solicitud inválida")
                .detalles("El sistema de calificación no ha sido encontrado")
                .build()));
    }
}
