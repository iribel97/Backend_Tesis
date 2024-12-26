package com.tesis.BackV2.services.contenido;

import com.tesis.BackV2.config.ApiResponse;
import com.tesis.BackV2.dto.contenido.EntregaDTO;
import com.tesis.BackV2.dto.doc.DocumentoDTO;
import com.tesis.BackV2.entities.contenido.Entrega;
import com.tesis.BackV2.entities.documentation.DocEntrega;
import com.tesis.BackV2.enums.EstadoEntrega;
import com.tesis.BackV2.exceptions.ApiException;
import com.tesis.BackV2.infra.MensajeHtml;
import com.tesis.BackV2.repositories.EstudianteRepo;
import com.tesis.BackV2.repositories.contenido.EntregaRepo;
import com.tesis.BackV2.repositories.documentation.DocEntregaRepo;
import com.tesis.BackV2.request.contenido.EntregaRequest;
import com.tesis.BackV2.request.contenido.NotaRequest;
import com.tesis.BackV2.request.documentation.DocumentoRequest;
import com.tesis.BackV2.services.CorreoServ;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Base64;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EntregaServ {

    @Autowired
    private CorreoServ emailService;

    private final MensajeHtml mensaje = new MensajeHtml();

    private final EntregaRepo repo;
    private final DocEntregaRepo docEntregaRepo;
    private final EstudianteRepo estRepo;

    // Crear Entrega
    @Transactional
    public ApiResponse<String> editarEntrega(EntregaRequest request) {
        Entrega entrega = repo.findById(request.getId()).orElseThrow(() -> new ApiException(ApiResponse.builder()
                .error(true)
                .codigo(400)
                .mensaje("Solicitud incorrecta")
                .detalles("La entrega no ha sido encontrada")
                .build()));

        entrega.setContenido(request.getContenido());
        if (!request.getContenido().isEmpty()) {
            // Evaluar si la fecha que entrega se encuentra entre las fechas de la asignación
            if (entrega.getAsignacion().getFechaInicio().isAfter(request.getFechaEntrega()) || entrega.getAsignacion().getFechaFin().isBefore(request.getFechaEntrega())) {
               entrega.setEstado(EstadoEntrega.Retrasado);
               emailService.enviarCorreo(entrega.getEstudiante().getUsuario().getEmail(), "Entrega retrasada", "mensaje");
            }
        }
        entrega.setEstado(!request.getContenido().isEmpty() ? EstadoEntrega.Entregado : EstadoEntrega.Pendiente);
        entrega.setFechaEntrega(request.getFechaEntrega());
        entrega.setHoraEntrega(request.getHoraEntrega());

        Entrega entregaGuardada = repo.save(entrega);

        if ( request.getDocumentos() != null) {
            // Agregar documento de entrega
            for (DocumentoRequest docRec : request.getDocumentos()) {
                docEntregaRepo.save(
                        DocEntrega.builder()
                                .nombre(docRec.getNombre())
                                .mime(docRec.getMime())
                                .entrega(entregaGuardada)
                                .estudiante(entregaGuardada.getEstudiante())
                                .contenido(Base64.getDecoder().decode(docRec.getBase64()))
                                .build()
                );
            }
        }
        return ApiResponse.<String> builder()
                .error(false)
                .codigo(200)
                .mensaje("Entrega creada")
                .detalles("La entrega ha sido registrada correctamente")
                .build();
    }

    // Eliminar Entrega
    @Transactional
    public ApiResponse<String> eliminarEntrega(Long id) {
        Entrega entrega = repo.findById(id).orElseThrow(() -> new ApiException(ApiResponse.builder()
                .error(true)
                .codigo(400)
                .mensaje("Solicitud incorrecta")
                .detalles("La entrega no ha sido encontrada")
                .build()));

        entrega.setHoraEntrega(null);
        entrega.setFechaEntrega(null);
        entrega.setContenido(null);
        entrega.setEstado(EstadoEntrega.Pendiente);

        repo.save(entrega);

        return ApiResponse.<String> builder()
                .error(false)
                .codigo(200)
                .mensaje("Entrega eliminada")
                .detalles("La entrega ha sido eliminada correctamente")
                .build();
    }

    // Cambiar de estado una entrega
    @Transactional
    public ApiResponse<String> cambiarEstadoEntrega(Long id, EstadoEntrega estado) {
        Entrega entrega = repo.findById(id).orElseThrow(() -> new ApiException(ApiResponse.builder()
                .error(true)
                .codigo(400)
                .mensaje("Solicitud incorrecta")
                .detalles("La entrega no ha sido encontrada")
                .build()));

        entrega.setEstado(estado);

        repo.save(entrega);

        return ApiResponse.<String> builder()
                .error(false)
                .codigo(200)
                .mensaje("Entrega actualizada")
                .detalles("La entrega ha sido actualizada correctamente")
                .build();
    }

    // Calificar
    @Transactional
    public ApiResponse<String> calificarEntrega(NotaRequest request) {
        Entrega entrega = repo.findById(request.getIdEntrega()).orElseThrow(() -> new ApiException(ApiResponse.builder()
                .error(true)
                .codigo(400)
                .mensaje("Solicitud incorrecta")
                .detalles("La entrega no ha sido encontrada")
                .build()));

        entrega.setNota(request.getNota());
        entrega.setEstado(EstadoEntrega.Calificado);

        repo.save(entrega);

        return ApiResponse.<String> builder()
                .error(false)
                .codigo(200)
                .mensaje("Entrega calificada")
                .detalles("La entrega ha sido calificada correctamente")
                .build();
    }

    // Traer entregas por asignación
    public List<EntregaDTO> traerPorAsignacion(Long idAsignacion) {
        return repo.findByAsignacion_Id(idAsignacion).stream().map(this::convertirDTO).toList();
    }

    // Traer una sola entrega
    public EntregaDTO traerPorId(Long idEntrega) {
        return convertirDTO(repo.findById(idEntrega).orElseThrow(() -> new ApiException(ApiResponse.builder()
                .error(true)
                .codigo(400)
                .mensaje("Solicitud incorrecta")
                .detalles("La entrega no ha sido encontrada")
                .build())));
    }

    // Traer entregas por asignación y estudiante
    public List<EntregaDTO> traerPorAsignacionYEstudiante(Long idAsignacion, long idEstudiante) {
        return repo.findByAsignacion_IdAndEstudiante_Id(idAsignacion, idEstudiante).stream().map(this::convertirDTO).toList();
    }


    /* ---- MÉTODOS PROPIOS ---- */
    private EntregaDTO convertirDTO(Entrega entrega) {
        List<DocEntrega> docEntrega = docEntregaRepo.findByEntrega(entrega);
        return EntregaDTO.builder()
                .id(entrega.getId())
                .contenido(entrega.getContenido())
                .nota(entrega.getNota() != null ? entrega.getNota() : String.valueOf(0))
                .estado(String.valueOf(entrega.getEstado()))
                .fechaEntrega(String.valueOf(entrega.getFechaEntrega()))
                .horaEntrega(String.valueOf(entrega.getHoraEntrega()))
                .nombresEstudiante(entrega.getEstudiante().getUsuario().getNombres() + " " + entrega.getEstudiante().getUsuario().getApellidos())
                .documentos(docEntrega.stream().map(this::convertirDocDTO).toList())
                .build();
    }

    private DocumentoDTO convertirDocDTO (DocEntrega doc) {
        return DocumentoDTO.builder()
                .id(doc.getId())
                .nombre(doc.getNombre())
                .mime(doc.getMime())
                .base64(Base64.getEncoder().encodeToString(doc.getContenido()))
                .build();
    }
}
