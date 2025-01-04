package com.tesis.BackV2.services;

import com.tesis.BackV2.config.ApiResponse;
import com.tesis.BackV2.dto.contenido.*;
import com.tesis.BackV2.dto.doc.DocumentoDTO;
import com.tesis.BackV2.dto.horarioConfig.AgendaDTO;
import com.tesis.BackV2.entities.*;
import com.tesis.BackV2.entities.contenido.*;
import com.tesis.BackV2.entities.documentation.DocContMateria;
import com.tesis.BackV2.entities.documentation.DocEntrega;
import com.tesis.BackV2.enums.EstadoEntrega;
import com.tesis.BackV2.exceptions.ApiException;
import com.tesis.BackV2.infra.MensajeHtml;
import com.tesis.BackV2.repositories.*;
import com.tesis.BackV2.repositories.contenido.*;
import com.tesis.BackV2.repositories.documentation.DocEntregaRepo;
import com.tesis.BackV2.repositories.documentation.DocMaterialApoyoRepo;
import com.tesis.BackV2.request.contenido.*;
import com.tesis.BackV2.request.documentation.DocumentoRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ContenidoServ {

    @Autowired
    private CorreoServ emailService;

    private final MensajeHtml mensaje = new MensajeHtml();

    private final AsignacionRepo repoAsig;
    private final CicloAcademicoRepo repoCicl;
    private final DistributivoRepo repoDis;
    private final DocMaterialApoyoRepo repoDoc;
    private final DocEntregaRepo repoDocEnt;
    private final EntregaRepo repoEnt;
    private final EstudianteRepo repoEst;
    private final HorarioRepo repoHor;
    private final MaterialApoyoRepo repoMatAp;
    private final MatriculaRepo repoMat;
    private final SistCalifRepo repoSisCalf;
    private final TemaRepo repoTem;
    private final UnidadRepo repo;

    /* -------------------------------------- UNIDAD -------------------------------------------- */
    // Crear una unidad
    @Transactional
    public ApiResponse<String> crearUnidad(UnidadRequest request){
        validarRequestUnidad(request);

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

    // Editar
    @Transactional
    public ApiResponse<String> editarUnidad(UnidadRequest request){
        validarRequestUnidad(request);

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

    private void validarRequestUnidad(UnidadRequest request){
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

    /* -------------------------------------- TEMA -------------------------------------------- */
    // Crear un Tema
    @Transactional
    public ApiResponse<String> crearTema(TemaRequest request){

        validarRequestTema(request);

        Tema tema = Tema.builder()
                .activo(request.isActivo())
                .tema(request.getTema())
                .detalle(request.getDetalle())
                .unidad(repo.findById(request.getIdUnidad()).orElseThrow(() -> new ApiException(ApiResponse.builder()
                        .error(true)
                        .codigo(404)
                        .mensaje("Error de validación")
                        .detalles("La unidad no existe")
                        .build())))
                .build();

        repoTem.save(tema);
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

        validarRequestTema(request);

        Tema tema = repoTem.findById(request.getId()).orElseThrow(() -> new ApiException(ApiResponse.builder()
                .error(true)
                .codigo(404)
                .mensaje("Error de validación")
                .detalles("El tema no existe")
                .build()));

        tema.setActivo(request.isActivo());
        tema.setTema(request.getTema());
        tema.setDetalle(request.getDetalle());
        tema.setUnidad(repo.findById(request.getIdUnidad()).orElseThrow(() -> new ApiException(ApiResponse.builder()
                .error(true)
                .codigo(404)
                .mensaje("Error de validación")
                .detalles("La unidad no existe")
                .build())));

        repoTem.save(tema);
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

        Tema tema = repoTem.findById(id).orElseThrow(() -> new ApiException(ApiResponse.builder()
                .error(true)
                .codigo(404)
                .mensaje("Error de validación")
                .detalles("El tema no existe")
                .build()));

        repoTem.delete(tema);
        return ApiResponse.<String>builder()
                .error(false)
                .codigo(200)
                .mensaje("Tema eliminado correctamente")
                .detalles("El tema se ha eliminado correctamente")
                .build();
    }

    private void validarRequestTema(TemaRequest request){
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

    /* -------------------------------------- MATERIAL DE APOYO -------------------------------------------- */
    // Crear material apoyo
    @Transactional
    public ApiResponse<String> crearMaterialApoyo(MaterialApoyoRequest request) {

        // Validar si el tema existe
        Tema tema = repoTem.findById(request.getIdTema())
                .orElseThrow(() -> new ApiException(ApiResponse.<String>builder()
                        .error(true)
                        .codigo(400)
                        .mensaje("Solicitud incorrecta")
                        .detalles("El tema no ha sido encontrado")
                        .build()));

        if ( !request.getLink().isEmpty() && request.getDocumento() != null) {
            throw new ApiException(ApiResponse.builder()
                    .error(true)
                    .codigo(400)
                    .mensaje("Solicitud invalida")
                    .detalles("Solo debe de ingresar un tipo de material")
                    .build());
        }

        long part = repoMatAp.countByTema_Id(tema.getId()) + 1;
        String nombre = "MaterialApoyo_"+tema.getTema()+"_pt"+part;

        MaterialApoyo material = new MaterialApoyo();
        material.setActivo(request.isActivo());
        material.setTema(tema);
        if ( request.getLink().isEmpty()) {
            material.setDocumento(guardarDoc(request.getDocumento(), nombre, tema));
        } else {
            material.setLink(request.getLink());
            material.setNombreLink(nombre);
        }
        repoMatAp.save(material);

        return ApiResponse.<String>builder()
                .error(false)
                .codigo(200)
                .mensaje("Material de apoyo registrado")
                .detalles("El material de apoyo ha sido registrado exitosamente")
                .build();
    }

    // Editar
    @Transactional
    public ApiResponse<String> editarMaterialApoyo (MaterialApoyoRequest request) {
        MaterialApoyo material = repoMatAp.findById(request.getId())
                .orElseThrow(() -> new ApiException(ApiResponse.<String>builder()
                        .error(true)
                        .codigo(400)
                        .mensaje("Solicitud incorrecta")
                        .detalles("El material de apoyo no ha sido encontrado")
                        .build()));

        material.setActivo(request.isActivo());

        if ( !material.getLink().isEmpty() ) {
            material.setLink(request.getLink());
        } else {
            if (request.getDocumento() != null) {
                DocContMateria doc = material.getDocumento();
                doc.setContenido(Base64.getDecoder().decode(request.getDocumento().getBase64()));
                doc.setMime(request.getDocumento().getMime());
                repoDoc.save(doc);
            }
        }
        repoMatAp.save(material);
        return ApiResponse.<String> builder()
                .error(false)
                .codigo(200)
                .mensaje("Edición completa")
                .detalles("Se ha editado correctamente elmaterial de apoyo")
                .build();
    }

    // Eliminar
    @Transactional
    public ApiResponse<String> eliminarMaterialApoyo(long id) {
        MaterialApoyo material = repoMatAp.findById(id)
                .orElseThrow(() -> new ApiException(ApiResponse.<String>builder()
                        .error(true)
                        .codigo(400)
                        .mensaje("Solicitud incorrecta")
                        .detalles("El material de apoyo no ha sido encontrado")
                        .build()));

        if (material.getDocumento() != null) {
            repoDoc.delete(material.getDocumento());
        }
        repoMatAp.delete(material);

        return ApiResponse.<String>builder()
                .error(false)
                .codigo(200)
                .mensaje("Material de apoyo eliminado")
                .detalles("El material de apoyo ha sido eliminado exitosamente")
                .build();
    }

    private DocContMateria guardarDoc(DocumentoRequest file, String titulo, Tema tema) {
        try {
            DocContMateria documento = DocContMateria.builder()
                    .nombre(titulo)
                    .contenido(Base64.getDecoder().decode(file.getBase64()))
                    .mime(file.getMime())
                    .build();
            return repoDoc.save(documento);
        } catch (RuntimeException e) {
            throw new ApiException(ApiResponse.<String>builder()
                    .error(true)
                    .codigo(500)
                    .mensaje("Error interno")
                    .detalles("Ha ocurrido un error al guardar el documento")
                    .build());
        }
    }


    /* -------------------------------------- ASIGNACION -------------------------------------------- */
    // Crear asignación
    @Transactional
    public ApiResponse<String> crearAsignacion (AsignacionRequest request) {

        validarDatosAsig(request);

        Tema tema = repoTem.findById(request.getIdTema()).get();
        SistemaCalificacion calif = repoSisCalf.findById(request.getCalif()).get();

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

        Asignacion asignacionGuardada = repoAsig.save(asignacion);

        // crear asignaciones para los estudiantes vacia
        List<Estudiante> students = estudiantes(tema.getUnidad().getDistributivo().getCurso().getId());
        students.forEach(estudiante -> {
            repoEnt.save(Entrega.builder()
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
                        return repoDoc.save(documento);
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
        validarDatosAsig(request);

        Asignacion asignacion = repoAsig.findById(request.getId()).orElseThrow(() -> new ApiException(ApiResponse.<String>builder()
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

        Asignacion asignacionGuardada = repoAsig.save(asignacion);

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
                        return repoDoc.save(documento);
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

    @Transactional
    public ApiResponse<String> ocultarAsignacion (long idAsignacion) {
        Asignacion asignacion = repoAsig.findById(idAsignacion).orElseThrow(() -> new ApiException(ApiResponse.<String>builder()
                .error(true)
                .codigo(400)
                .mensaje("Solicitud inválida")
                .detalles("La asignación con id " + idAsignacion + " no ha sido encontrada")
                .build()));

        asignacion.setActivo(false);

        repoAsig.save(asignacion);

        return ApiResponse.<String>builder()
                .error(false)
                .codigo(200)
                .mensaje("Asignación eliminada")
                .detalles("La asignación ha sido eliminada exitosamente")
                .build();
    }

    // traer asignacion por id
    public AsignacionDTO traerPorId (long idAsignacion, Long idEst) {
        return convertirAsigDTOEstudiante(repoAsig.findById(idAsignacion).orElseThrow(() -> new ApiException(ApiResponse.<String>builder()
                .error(true)
                .codigo(400)
                .mensaje("Solicitud inválida")
                .detalles("La asignación con id " + idAsignacion + " no ha sido encontrada")
                .build())), idEst);
    }

    // convertir a DTO para un estudiante
    private AsignacionDTO convertirAsigDTOEstudiante(Asignacion request, Long idEst) {
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
                .documentos(repoDoc.findByAsignacion_Id(request.getId()).stream()
                        .map(this::convertirDoc)
                        .collect(Collectors.toList()))
                .entregaEst(traerPorAsignacionYEstudiante(request.getId(), idEst))
                .build();
    }

    // Validar atributos
    private void validarDatosAsig(AsignacionRequest request) {
        // Traer el tema
        repoTem.findById(request.getIdTema()).orElseThrow(() -> new ApiException(ApiResponse.<String>builder()
                .error(true)
                .codigo(400)
                .mensaje("Solicitud inválida")
                .detalles("El tema con id " + request.getIdTema() + " no ha sido encontrado")
                .build()));

        // validar el id del sistema de calificación
        repoSisCalf.findById(request.getCalif()).orElseThrow( () -> new ApiException(ApiResponse.<String>builder()
                .error(true)
                .codigo(400)
                .mensaje("Solicitud inválida")
                .detalles("El sistema de calificación no ha sido encontrado")
                .build()));
    }

    private List<Estudiante> estudiantes (Long idCurso) {
        List<Estudiante> estudents = new ArrayList<>();

        List<Matricula> matriculas = repoMat.findByCicloAndCurso_Id(repoCicl.findTopByOrderByIdDesc(), idCurso);

        matriculas.forEach(matricula -> {
            estudents.add(repoEst.findById(matricula.getEstudiante().getId()).get());
        });

        return estudents;
    }

    /* -------------------------------------- Entrega -------------------------------------------- */
    // Crear Entrega
    @Transactional
    public ApiResponse<String> editarEntrega(EntregaRequest request) {
        Entrega entrega = repoEnt.findById(request.getId()).orElseThrow(() -> new ApiException(ApiResponse.builder()
                .error(true)
                .codigo(400)
                .mensaje("Solicitud incorrecta")
                .detalles("La entrega no ha sido encontrada")
                .build()));
        if (!request.getContenido().isEmpty()) {
            // Evaluar si la fecha que entrega es mayor a la fecha de fin de la asignación
            if (request.getFechaEntrega().isAfter(entrega.getAsignacion().getFechaFin())) {
                Representante representante = entrega.getEstudiante().getRepresentante();

                entrega.setEstado(EstadoEntrega.Retrasado);

                emailService.enviarCorreo( representante.getUsuario().getEmail(),
                        "Entrega retrasada",
                        mensaje.mensajeEntregaTardeAsignacion( representante.getUsuario().getApellidos() + " " + representante.getUsuario().getNombres(),
                                entrega.getEstudiante().getUsuario().getApellidos() + " " + entrega.getEstudiante().getUsuario().getNombres(),
                                entrega.getAsignacion().getNombre(),
                                String.valueOf(entrega.getAsignacion().getFechaFin()),
                                String.valueOf(request.getFechaEntrega()) )
                );
            } else {
                entrega.setEstado(EstadoEntrega.Entregado);
            }
        }
        entrega.setContenido(request.getContenido());
        entrega.setFechaEntrega(request.getFechaEntrega());
        entrega.setHoraEntrega(request.getHoraEntrega());

        Entrega entregaGuardada = repoEnt.save(entrega);

        if ( request.getDocumentos() != null) {
            // Agregar documento de entrega
            for (DocumentoRequest docRec : request.getDocumentos()) {
                repoDocEnt.save(
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
        Entrega entrega = repoEnt.findById(id).orElseThrow(() -> new ApiException(ApiResponse.builder()
                .error(true)
                .codigo(400)
                .mensaje("Solicitud incorrecta")
                .detalles("La entrega no ha sido encontrada")
                .build()));

        entrega.setHoraEntrega(null);
        entrega.setFechaEntrega(null);
        entrega.setContenido(null);
        entrega.setEstado(EstadoEntrega.Pendiente);

        repoEnt.save(entrega);

        return ApiResponse.<String> builder()
                .error(false)
                .codigo(200)
                .mensaje("Entrega eliminada")
                .detalles("La entrega ha sido eliminada correctamente")
                .build();
    }

    // Calificar
    @Transactional
    public ApiResponse<String> calificarEntrega(NotaRequest request) {
        // Validar y obtener entrega
        Entrega entrega = repoEnt.findById(request.getIdEntrega())
                .orElseThrow(() -> new ApiException(ApiResponse.builder()
                        .error(true)
                        .codigo(400)
                        .mensaje("Solicitud incorrecta")
                        .detalles("La entrega no ha sido encontrada")
                        .build()));

        // Validar nota
        if (request.getNota() == null) {
            throw new ApiException(ApiResponse.builder()
                    .error(true)
                    .codigo(400)
                    .mensaje("Nota no proporcionada")
                    .detalles("La nota de la entrega no puede ser nula")
                    .build());
        }

        entrega.setNota(request.getNota());

        // Verificar si la nota es menor a la base
        if (Float.valueOf(request.getNota()) < Float.valueOf(entrega.getAsignacion().getCalif().getBase())) {
            enviarCorreoNotaBaja(entrega, request.getNota());
        }

        // Cambiar estado de la entrega
        entrega.setEstado(EstadoEntrega.Calificado);

        // Guardar la entrega actualizada
        repoEnt.save(entrega);

        // Respuesta de éxito
        return ApiResponse.<String>builder()
                .error(false)
                .codigo(200)
                .mensaje("Entrega calificada")
                .detalles("La entrega ha sido calificada correctamente")
                .build();
    }

    // Traer entregas por asignación y estudiante
    public EntregaDTO traerPorAsignacionYEstudiante(Long idAsignacion, long idEstudiante) {
        return convertirDTO(repoEnt.findByAsignacion_IdAndEstudiante_Id(idAsignacion, idEstudiante));
    }

    private EntregaDTO convertirDTO(Entrega entrega) {
        List<DocEntrega> docEntrega = repoDocEnt.findByEntrega(entrega);
        return EntregaDTO.builder()
                .id(entrega.getId())
                .contenido(entrega.getContenido())
                .nota(entrega.getNota() != null ? entrega.getNota() : "")
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


    private void enviarCorreoNotaBaja(Entrega entrega, String nota) {
        Representante representante = entrega.getEstudiante().getRepresentante();
        if (representante != null && representante.getUsuario() != null) {
            String email = representante.getUsuario().getEmail();
            if (email != null && !email.isEmpty()) {
                try {
                    emailService.enviarCorreo(
                            email,
                            "Entrega calificada",
                            mensaje.mensajeBajaNotaEntrega(
                                    entrega.getEstudiante().getUsuario().getApellidos() + " " +
                                            entrega.getEstudiante().getUsuario().getNombres(),
                                    entrega.getAsignacion().getTema().getUnidad().getDistributivo().getMateria().getNombre(),
                                    entrega.getAsignacion().getNombre(),
                                    nota,
                                    String.valueOf(entrega.getAsignacion().getCalif().getBase())
                            )
                    );
                } catch (Exception e) {
                    // Manejo del error de envío de correo
                    throw new ApiException(ApiResponse.builder()
                            .error(true)
                            .codigo(500)
                            .mensaje("Solicitud invalida")
                            .detalles("No se ha podido enviar el correo al representante")
                            .build());
                }
            }
        }
    }

    public ContenidoDTO contenidoMateria (Long idDistributivo) {
        // Validar que el id exista
        Distributivo distributivo = repoDis.findById(idDistributivo).orElseThrow(() -> new ApiException(ApiResponse.builder()
                .error(true)
                .mensaje("Solicitud incorrecta")
                .codigo(400)
                .detalles("Distributivo no encontrado")
                .build()
        ));

        ContenidoDTO contenido = new ContenidoDTO();

        contenido.setIdDistributivo(distributivo.getId());
        contenido.setNombreMateria(distributivo.getMateria().getNombre());
        contenido.setDocenteNombres(distributivo.getDocente().getUsuario().getNombres());
        contenido.setDocenteApellidos(distributivo.getDocente().getUsuario().getApellidos());
        contenido.setHorarios(getAgendaByDocente(distributivo.getId()));
        contenido.setUnidades(getUnidadesByMateriaActiva(distributivo.getId()));

        return contenido;
    }


    // Agenda DTO
    private List<AgendaDTO> getAgendaByDocente(long idDis) {
        List<Horario> horarios = repoHor.findByDistributivo_Id(idDis);
        List<AgendaDTO> agendas = new ArrayList<>();

        for (Horario horario : horarios) {
            // Busca si ya existe un registro para el día
            AgendaDTO agendaExistente = agendas.stream()
                    .filter(agenda -> agenda.getDia().equals(horario.getDiaSemana()))
                    .findFirst()
                    .orElse(null);

            if (agendaExistente != null) {
                // Si ya existe, actualiza la hora fin
                agendaExistente.setHoraFin(String.valueOf(horario.getHorario().getHoraFin()));
            } else {
                // Si no existe, agrega un nuevo registro
                agendas.add(AgendaDTO.builder()
                        .dia(horario.getDiaSemana())
                        .horaInicio(String.valueOf(horario.getHorario().getHoraInicio()))
                        .horaFin(String.valueOf(horario.getHorario().getHoraFin()))
                        .build());
            }
        }

        return agendas;
    }

    /*  ---- ESTUDIANTE ---- */
    // Unidades activas de una materia
    private List<UnidadesDTO> getUnidadesByMateriaActiva(long idDis) {
        List<Unidad> unidades = repo.findByDistributivo_IdAndActivo(idDis, true);

        List<UnidadesDTO> uniDTO = new ArrayList<>();
        for (Unidad unidad : unidades) {
            uniDTO.add(UnidadesDTO.builder()
                    .idUnidad(unidad.getId())
                    .nombre(unidad.getTitulo())
                    .activo(unidad.isActivo())
                    .contenido(getTemasByUnidadActivo(unidad.getId()))
                    .build());
        }

        return uniDTO;
    }

    // Temas de una unidad
    private List<TemaDTO> getTemasByUnidadActivo(long idUnidad) {
        List<Tema> temas = repoTem.findByUnidadIdAndActivo(idUnidad, true);

        List<TemaDTO> temaDTO = new ArrayList<>();
        for (Tema tema : temas) {
            temaDTO.add(TemaDTO.builder()
                    .idTema(tema.getId())
                    .activo(tema.isActivo())
                    .nombreTema(tema.getTema())
                    .descripcion(tema.getDetalle())
                    .materiales(getMaterialByTemaActivo(tema.getId()))
                    .asignaciones(getAsignacionesByTemaActivo(tema.getId()))
                    .build());
        }

        return temaDTO;
    }

    // Materiales de apoyo de un tema
    private List<MaterialApoyoDTO> getMaterialByTemaActivo(long idTema) {
        List<MaterialApoyo> materiales = repoMatAp.findByTema_IdAndActivo(idTema, true);

        List<MaterialApoyoDTO> matDTO = new ArrayList<>();
        for (MaterialApoyo material : materiales) {
            matDTO.add(MaterialApoyoDTO.builder()
                    .idMaterial(material.getId())
                    .activo(material.isActivo())
                    .link(material.getLink())
                    .nombreLink(material.getNombreLink())
                    .documento(material.getDocumento() != null ? convertirDoc(material.getDocumento()) : null)
                    .build());
        }

        return matDTO;
    }

    private DocumentoDTO convertirDoc (DocContMateria doc) {
        return DocumentoDTO.builder()
                .id(doc.getId())
                .nombre(doc.getNombre())
                .base64(Base64.getEncoder().encodeToString(doc.getContenido()))
                .mime(doc.getMime())
                .build();
    }

    // Asignaciones de un tema
    private List<AsignacionesDTO> getAsignacionesByTemaActivo(long idTema) {
        List<Asignacion> asignaciones = repoAsig.findByTema_IdAndActivo(idTema, true);

        List<AsignacionesDTO> asigDTO = new ArrayList<>();
        for (Asignacion asignacion : asignaciones) {
            asigDTO.add(AsignacionesDTO.builder()
                    .idAsignacion(asignacion.getId())
                    .activo(asignacion.isActivo())
                    .nombre(asignacion.getNombre())
                    .descripcion(asignacion.getDescripcion())
                    .fechaFin(String.valueOf(asignacion.getFechaFin()))
                    .horaFin(String.valueOf(asignacion.getHoraFin()))
                    .documentos(repoDoc.findByAsignacion_Id(asignacion.getId()).stream()
                            .map(this::convertirDoc)
                            .toList())
                    .build());
        }

        return asigDTO;
    }


    /* -------------------------- CODIGO RESERVADO ------------------------------------ */
    /* UNIDAD
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

    private UnidadDTO convertirUnidadADTO(Unidad unidad){
        return UnidadDTO.builder()
                .id(unidad.getId())
                .tema(unidad.getTitulo())
                .activo(unidad.isActivo())
                .build();
    }
     */

    /* TEMA
    // Listar temas por unidad
    public List<TemaDTO> obtenerTemas(long idUnidad){
        return repoTem.findByUnidadId(idUnidad).stream().map(this::convertirADTO).toList();
    }

    // Traer un tema
    public TemaDTO obtenerTema(long id){
        return convertirADTO(repoTem.findById(id).orElseThrow(() -> new ApiException(ApiResponse.builder()
                .error(true)
                .codigo(404)
                .mensaje("Error de validación")
                .detalles("El tema no existe")
                .build())));
    }

    // Traer temas activos por unidad
    public List<TemaDTO> obtenerTemasActivos(long idUnidad){
        return repoTem.findByUnidadIdAndActivo(idUnidad, true).stream().map(this::convertirADTO).toList();
    }

    // Traer un tema activo
    public TemaDTO obtenerTemaActivo(long id){
        return convertirADTO(repoTem.findByIdAndActivo(id, true));
    }

     private TemaDTO convertirTemaADTO(Tema tema){
        return TemaDTO.builder()
                .idTema(tema.getId())
                .activo(tema.isActivo())
                .nombreTema(tema.getTema())
                .descripcion(tema.getDetalle())
                .build();
    }

     */

    /* MATERIAL DE APOYO
    // traer por tema
    public List<MaterialApoyoDTO> obtenerPorTema(long idTema) {
        Tema tema = repoTem.findById(idTema)
                .orElseThrow(() -> new ApiException(ApiResponse.<String>builder()
                        .error(true)
                        .codigo(400)
                        .mensaje("Solicitud incorrecta")
                        .detalles("El tema no ha sido encontrado")
                        .build()));

        return repoMatAp.findByTema_Id(tema.getId()).stream()
                .map(this::convertirDTO)
                .toList();
    }

    // traer por tema y activo
    public List<MaterialApoyoDTO> obtenerPorTemaActivo(long idTema, boolean activo) {
        Tema tema = repoTem.findById(idTema)
                .orElseThrow(() -> new ApiException(ApiResponse.<String>builder()
                        .error(true)
                        .codigo(400)
                        .mensaje("Solicitud incorrecta")
                        .detalles("El tema no ha sido encontrado")
                        .build()));

        return repoMatAp.findByTema_IdAndActivo(tema.getId(), activo).stream()
                .map(this::convertirDTO)
                .toList();
    }

    private MaterialApoyoDTO convertirMaterialApoyoDTO (MaterialApoyo material) {
        return MaterialApoyoDTO.builder()
                .idMaterial(material.getId())
                .activo(material.isActivo())
                .link(material.getLink())
                .nombreLink(material.getNombreLink())
                .documento(material.getDocumento() != null ? convertirDoc(material.getDocumento()) : null)
                .build();
    }

    private DocumentoDTO convertirDoc (DocContMateria doc) {
        return DocumentoDTO.builder()
                .id(doc.getId())
                .nombre(doc.getNombre())
                .base64(Base64.getEncoder().encodeToString(doc.getContenido()))
                .mime(doc.getMime())
                .build();
    }
     */
}