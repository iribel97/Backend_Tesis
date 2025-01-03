package com.tesis.BackV2.services.contenido;

import com.tesis.BackV2.config.ApiResponse;
import com.tesis.BackV2.dto.contenido.AsignacionDTO;
import com.tesis.BackV2.dto.doc.DocumentoDTO;
import com.tesis.BackV2.entities.Estudiante;
import com.tesis.BackV2.entities.Matricula;
import com.tesis.BackV2.entities.SistemaCalificacion;
import com.tesis.BackV2.entities.contenido.Asignacion;
import com.tesis.BackV2.entities.contenido.Entrega;
import com.tesis.BackV2.entities.contenido.Tema;
import com.tesis.BackV2.entities.documentation.DocContMateria;
import com.tesis.BackV2.enums.EstadoEntrega;
import com.tesis.BackV2.exceptions.ApiException;
import com.tesis.BackV2.repositories.CicloAcademicoRepo;
import com.tesis.BackV2.repositories.EstudianteRepo;
import com.tesis.BackV2.repositories.MatriculaRepo;
import com.tesis.BackV2.repositories.SistCalifRepo;
import com.tesis.BackV2.repositories.contenido.AsignacionRepo;
import com.tesis.BackV2.repositories.contenido.EntregaRepo;
import com.tesis.BackV2.repositories.contenido.TemaRepo;
import com.tesis.BackV2.repositories.documentation.DocMaterialApoyoRepo;
import com.tesis.BackV2.request.contenido.AsignacionRequest;
import com.tesis.BackV2.request.documentation.DocumentoRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
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
    private final EntregaRepo entregaRepo;
    private final EstudianteRepo estRepo;
    private final MatriculaRepo matRepo;
    private final CicloAcademicoRepo cicloRepo;


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

        // crear asignaciones para los estudiantes vacia
        List<Estudiante> students = estudiantes(tema.getUnidad().getDistributivo().getCurso().getId());
        students.forEach(estudiante -> {
            entregaRepo.save(Entrega.builder()
                    .activo(asignacionGuardada.isActivo())
                    .estado(EstadoEntrega.Pendiente)
                    .asignacion(asignacionGuardada)
                    .estudiante(estudiante)
                    .build());
        });


        // Guardar los documentos asociados
        if (request.getDocumentos() != null && !request.getDocumentos().isEmpty()) {
            List<DocContMateria> documentos = request.getDocumentos().stream()
                    .map(doc -> {
                        DocContMateria documento = DocContMateria.builder()
                                .nombre(doc.getNombre())
                                .mime(doc.getMime())
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

    // traer asignaciones por tema
    public List<AsignacionDTO> traerPorTema (long idTema) {
        return repo.findByTema_Id(idTema).stream()
                .map(this::convertirDTO)
                .collect(Collectors.toList());
    }

    // traer asignaciones por tema y activas
    public List<AsignacionDTO> traerPorTemaActivo (long idTema, boolean activo) {
        return repo.findByTema_IdAndActivo(idTema, activo).stream()
                .map(this::convertirDTO)
                .collect(Collectors.toList());
    }

    // traer asignacion por id
    public AsignacionDTO traerPorId (long idAsignacion) {
        return convertirDTO(repo.findById(idAsignacion).orElseThrow(() -> new ApiException(ApiResponse.<String>builder()
                .error(true)
                .codigo(400)
                .mensaje("Solicitud inválida")
                .detalles("La asignación con id " + idAsignacion + " no ha sido encontrada")
                .build())));
    }

    // Ocultar asignación
    @Transactional
    public ApiResponse<String> ocultarAsignacion (long idAsignacion) {
        Asignacion asignacion = repo.findById(idAsignacion).orElseThrow(() -> new ApiException(ApiResponse.<String>builder()
                .error(true)
                .codigo(400)
                .mensaje("Solicitud inválida")
                .detalles("La asignación con id " + idAsignacion + " no ha sido encontrada")
                .build()));

        asignacion.setActivo(false);

        repo.save(asignacion);

        return ApiResponse.<String>builder()
                .error(false)
                .codigo(200)
                .mensaje("Asignación eliminada")
                .detalles("La asignación ha sido eliminada exitosamente")
                .build();
    }


    /* ---- METODOS PROPIOS DEL SERVICIO ---- */
    // Validar atributos
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

    // Convertir a DTO la asignación para enviarla
    private AsignacionDTO convertirDTO (Asignacion request) {
        return AsignacionDTO.builder()
                .id(request.getId())
                .activo(request.isActivo())
                .nombre(request.getNombre())
                .descripcion(request.getDescripcion())
                .fechaInicio(String.valueOf(request.getFechaInicio()))
                .horaInicio(String.valueOf(request.getHoraInicio()))
                .fechaFin(String.valueOf(request.getFechaFin()))
                .horaFin(String.valueOf(request.getHoraFin()))
                .notaMax(request.getCalif().getMaximo())
                .documentos(docMatRepo.findByAsignacion_Id(request.getId()).stream()
                        .map(this::convertirDocDTO)
                        .collect(Collectors.toList()))
                .build();

    }

    // Convertir a DTO el documento
    private DocumentoDTO convertirDocDTO (DocContMateria request) {
        return DocumentoDTO.builder()
                .id(request.getId())
                .nombre(request.getNombre())
                .mime(request.getMime())
                .base64(Base64.getEncoder().encodeToString(request.getContenido()))
                .build();
    }

    // Crear los registros para la entrega
    private List<Estudiante> estudiantes (Long idCurso) {
        List<Estudiante> estudents = new ArrayList<>();

        List<Matricula> matriculas = matRepo.findByCicloAndCurso_Id(cicloRepo.findTopByOrderByIdDesc(), idCurso);

        matriculas.forEach(matricula -> {
            estudents.add(estRepo.findById(matricula.getEstudiante().getId()).get());
        });

        return estudents;
    }

}
