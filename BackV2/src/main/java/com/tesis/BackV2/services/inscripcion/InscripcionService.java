package com.tesis.BackV2.services.inscripcion;

import com.tesis.BackV2.config.ApiResponse;
import com.tesis.BackV2.config.auth.AuthService;
import com.tesis.BackV2.dto.InscripcionDTO;
import com.tesis.BackV2.dto.doc.DocumentoDTO;
import com.tesis.BackV2.entities.Inscripcion;
import com.tesis.BackV2.entities.Matricula;
import com.tesis.BackV2.entities.documentation.DocCedula;
import com.tesis.BackV2.entities.documentation.DocCertifNota;
import com.tesis.BackV2.entities.documentation.DocServBasicos;
import com.tesis.BackV2.entities.documentation.InscripPruebaAdicional;
import com.tesis.BackV2.entities.temp.TempMatricula;
import com.tesis.BackV2.enums.*;
import com.tesis.BackV2.exceptions.ApiException;
import com.tesis.BackV2.infra.MensajeHtml;
import com.tesis.BackV2.repositories.*;
import com.tesis.BackV2.repositories.documentation.DocCedulaRepo;
import com.tesis.BackV2.repositories.documentation.DocCertifNotaRepo;
import com.tesis.BackV2.repositories.documentation.DocServBasicosRepo;
import com.tesis.BackV2.repositories.documentation.InscripPruebaAdicionalRepo;
import com.tesis.BackV2.repositories.temp.TempMatriculaRpo;
import com.tesis.BackV2.request.InscripcionRequest;
import com.tesis.BackV2.request.MatriculacionRequest;
import com.tesis.BackV2.services.CorreoServ;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class InscripcionService {
    @Autowired
    private CorreoServ emailService;
    @Autowired
    private AuthService authService;

    private final InscripcionRepo repo;
    private final DocCedulaRepo repoCedula;
    private final DocCertifNotaRepo repoNotas;
    private final DocServBasicosRepo repoServBasicos;
    private final RepresentanteRepo repoRepresentante;
    private final UsuarioRepo repoUsuario;
    private final CicloAcademicoRepo repoCicloAcademico;
    private final InscripPruebaAdicionalRepo repoPruebaAdicional;
    private final MatriculaRepo repoMatricula;
    private final CursoRepo repoCurso;
    private final GradoRepo repoGrado;
    private final TempMatriculaRpo repoTempM;

    private final MensajeHtml mensaje = new MensajeHtml();

    // CREAR LA INSCRIPCION
    public ApiResponse<String> inscripcion(InscripcionRequest request,
                                           MultipartFile cedulaEstudiante,
                                           MultipartFile cedulaPadre,
                                           MultipartFile cedulaMadre,
                                           MultipartFile certificadoNotas,
                                           MultipartFile serviciosBasicos) throws IOException {
        try {
            validarInscripcion(request.getCedula());

            Inscripcion inscripcion = buildInscripcion(request, cedulaEstudiante, cedulaPadre, cedulaMadre, certificadoNotas, serviciosBasicos);

            repo.save(inscripcion);

        } catch (IOException e) {
            return ApiResponse.<String>builder()
                    .error(true)
                    .codigo(400)
                    .mensaje("Error al guardar los documentos")
                    .detalles(e.getMessage())
                    .build();
        }

        return ApiResponse.<String>builder()
                .error(false)
                .codigo(200)
                .mensaje("Inscripción creada")
                .detalles("La inscripción fue creada con éxito")
                .build();
    }

    // Método auxiliar para construir la inscripción
    private Inscripcion buildInscripcion(InscripcionRequest request,
                                         MultipartFile cedulaEstudiante,
                                         MultipartFile cedulaPadre,
                                         MultipartFile cedulaMadre,
                                         MultipartFile certificadoNotas,
                                         MultipartFile serviciosBasicos) throws IOException {
        return Inscripcion.builder()
                .cedula(request.getCedula())
                .nombres(request.getNombres())
                .apellidos(request.getApellidos())
                .email(request.getEmail())
                .telefono(request.getTelefono())
                .direccion(request.getDireccion())
                .fechaNacimiento(request.getFechaNacimiento())
                .genero(request.getGenero())
                .nombresPadre(request.getNombresPadre())
                .apellidosPadre(request.getApellidosPadre())
                .correoPadre(request.getCorreoPadre())
                .telefonoPadre(request.getTelefonoPadre())
                .ocupacionPadre(request.getOcupacionPadre())
                .nombresMadre(request.getNombresMadre())
                .apellidosMadre(request.getApellidosMadre())
                .correoMadre(request.getCorreoMadre())
                .telefonoMadre(request.getTelefonoMadre())
                .ocupacionMadre(request.getOcupacionMadre())
                .estado(EstadoInscripcion.Pendiente)
                .fechaInscripcion(java.time.LocalDate.now())
                .cilo(repoCicloAcademico.findTopByOrderByIdDesc())
                .grado(repoGrado.findByNombre(request.getGrado()))
                .cedulaEstudiante(saveDocCedula(cedulaEstudiante))
                .cedulaPadre(saveDocCedula(cedulaPadre))
                .cedulaMadre(saveDocCedula(cedulaMadre))
                .certificadoNotas(saveDocCertifNota(certificadoNotas))
                .serviciosBasicos(saveDocServBasicos(serviciosBasicos))
                .representante(repoRepresentante.findByUsuarioCedula(request.getRepresentanteId()))
                .build();
    }

    // Métodos auxiliares para guardar documentos en sus respectivos repositorios
    private DocCedula saveDocCedula(MultipartFile file) throws IOException {
        return repoCedula.save(DocCedula.builder()
                .nombre(file.getOriginalFilename())
                .contenido(file.getBytes())
                .mime(file.getContentType())
                .build());
    }

    private DocCertifNota saveDocCertifNota(MultipartFile file) throws IOException {
        return repoNotas.save(DocCertifNota.builder()
                .nombre(file.getOriginalFilename())
                .contenido(file.getBytes())
                .mime(file.getContentType())
                .build());
    }

    private DocServBasicos saveDocServBasicos(MultipartFile file) throws IOException {
        return repoServBasicos.save(DocServBasicos.builder()
                .nombre(file.getOriginalFilename())
                .contenido(file.getBytes())
                .mime(file.getContentType())
                .build());
    }

    // EDITAR LA INSCRIPCION
    public ApiResponse<String> editarInscripcion(InscripcionRequest request,
                                                 MultipartFile cedulaEstudiante,
                                                 MultipartFile cedulaPadre,
                                                 MultipartFile cedulaMadre,
                                                 MultipartFile certificadoNotas,
                                                 MultipartFile serviciosBasicos) throws IOException {
        try {
            Inscripcion inscripcion = repo.findById(request.getCedula())
                    .orElseThrow(() -> new ApiException(ApiResponse.<String>builder()
                            .error(true)
                            .codigo(404)
                            .mensaje("Solicitud inválida")
                            .detalles("La inscripción con la cédula " + request.getCedula() + " no existe")
                            .build()));

            if (inscripcion.getEstado().equals(EstadoInscripcion.Aceptado)) {
                return ApiResponse.<String>builder()
                        .error(true)
                        .codigo(400)
                        .mensaje("Error al editar la inscripción")
                        .detalles("La inscripción ya fue aceptada")
                        .build();
            }

            actualizarInscripcion(inscripcion, request);
            repo.save(inscripcion);

            actualizarDocumentos(inscripcion, cedulaEstudiante, cedulaPadre, cedulaMadre, certificadoNotas, serviciosBasicos);

            return ApiResponse.<String>builder()
                    .error(false)
                    .codigo(200)
                    .mensaje("Inscripción editada")
                    .detalles("La inscripción fue editada con éxito")
                    .build();

        } catch (IOException e) {
            return ApiResponse.<String>builder()
                    .error(true)
                    .codigo(400)
                    .mensaje("Error al guardar los documentos")
                    .detalles(e.getMessage())
                    .build();
        }
    }

    // Método auxiliar para actualizar la inscripción
    private void actualizarInscripcion(Inscripcion inscripcion, InscripcionRequest request) {
        inscripcion.setNombres(request.getNombres());
        inscripcion.setApellidos(request.getApellidos());
        inscripcion.setEmail(request.getEmail());
        inscripcion.setTelefono(request.getTelefono());
        inscripcion.setDireccion(request.getDireccion());
        inscripcion.setFechaNacimiento(request.getFechaNacimiento());
        inscripcion.setNombresPadre(request.getNombresPadre());
        inscripcion.setApellidosPadre(request.getApellidosPadre());
        inscripcion.setCorreoPadre(request.getCorreoPadre());
        inscripcion.setTelefonoPadre(request.getTelefonoPadre());
        inscripcion.setOcupacionPadre(request.getOcupacionPadre());
        inscripcion.setNombresMadre(request.getNombresMadre());
        inscripcion.setApellidosMadre(request.getApellidosMadre());
        inscripcion.setCorreoMadre(request.getCorreoMadre());
        inscripcion.setTelefonoMadre(request.getTelefonoMadre());
        inscripcion.setOcupacionMadre(request.getOcupacionMadre());
        inscripcion.setEstado(EstadoInscripcion.Pendiente);
    }

    // Método auxiliar para actualizar documentos
    private void actualizarDocumentos(Inscripcion inscripcion,
                                      MultipartFile cedulaEstudiante,
                                      MultipartFile cedulaPadre,
                                      MultipartFile cedulaMadre,
                                      MultipartFile certificadoNotas,
                                      MultipartFile serviciosBasicos) throws IOException {
        editCedula(inscripcion.getCedulaEstudiante().getId(), cedulaEstudiante);
        editCedula(inscripcion.getCedulaPadre().getId(), cedulaPadre);
        editCedula(inscripcion.getCedulaMadre().getId(), cedulaMadre);
        editCertifNotas(inscripcion.getCertificadoNotas().getId(), certificadoNotas);
        editServBasicos(inscripcion.getServiciosBasicos().getId(), serviciosBasicos);
    }

    // Editar el estado
    @Transactional
    public ApiResponse<String> cambiarEstadoInscripcion(String cedula, EstadoInscripcion estado, String aula) {
        Inscripcion inscripcion = repo.findById(cedula).orElseThrow(() -> new ApiException(ApiResponse.<String>builder()
                .error(true)
                .codigo(404)
                .mensaje("Solicitud invalida")
                .detalles("La inscripción con la cédula " + cedula + " no existe")
                .build()));

        if (estado.equals(EstadoInscripcion.Aceptado)) {
            if (inscripcion.getCilo().getInscripConfig().isRequierePruebas()) {
                generarPruebaAdicional(inscripcion, TipoPrueba.CONOCIMIENTO, LocalDate.now().plusDays(7));
                inscripcion.setEstado(EstadoInscripcion.Prueba);
                repoTempM.save(TempMatricula.builder()
                        .cedula(inscripcion.getCedula())
                        .grado(inscripcion.getGrado().getNombre())
                        .paralelo(aula)
                        .build());
            } else {
                matricularEstudiante(inscripcion, aula);
                inscripcion.setEstado(estado);
            }
        } else {
            inscripcion.setEstado(estado);
        }

        repo.save(inscripcion);

        return ApiResponse.<String>builder()
                .error(false)
                .codigo(200)
                .mensaje("Inscripción " + estado)
                .detalles("La inscripción fue " + estado + " con éxito")
                .build();
    }

    // Eliminar inscripción
    public ApiResponse<String> eliminarInscripcion(String cedula){
        Inscripcion inscripcion = repo.findById(cedula).orElseThrow(() -> new ApiException(ApiResponse.<String>builder()
                .error(true)
                .codigo(404)
                .mensaje("Solicitud invalida")
                .detalles("La inscripcion con la cedula " + cedula + " no existe")
                .build()));

        // validar que la inscripción no se encuentre aceptada
        if (inscripcion.getEstado().equals(EstadoInscripcion.Aceptado)) {
            return ApiResponse.<String>builder()
                    .error(true)
                    .codigo(400)
                    .mensaje("Solivitud invslida")
                    .detalles("La inscripcion ya fue aceptada")
                    .build();
        }
        repo.delete(inscripcion);

        repoCedula.delete( inscripcion.getCedulaEstudiante() );
        repoCedula.delete( inscripcion.getCedulaPadre() );
        repoCedula.delete( inscripcion.getCedulaMadre() );
        repoNotas.delete( inscripcion.getCertificadoNotas() );
        repoServBasicos.delete( inscripcion.getServiciosBasicos() );


        return ApiResponse.<String>builder()
                .error(false)
                .codigo(200)
                .mensaje("Solicitud procesada")
                .detalles("La inscripcion fue eliminada con exito")
                .build();

    }

    // Listar por representante
    public List<InscripcionDTO> listarPorRepresentante(String cedula) {
        List<Inscripcion> inscripciones = repo.findByRepresentante_Usuario_Cedula(cedula);
        List<InscripcionDTO> dtos = new ArrayList<>();

        for (Inscripcion inscripcion : inscripciones) {
            dtos.add(convertirInscripcion(inscripcion));
        }

        return dtos;
    }

    // Listar inscripciones pendientes
    public List<InscripcionDTO> getInscripcionesPendientes() {
        List<Inscripcion> inscripciones = repo.findByEstadoPendiente();
        List<InscripcionDTO> dtos = new ArrayList<>();

        for (Inscripcion inscripcion : inscripciones) {
            dtos.add(convertirInscripcion(inscripcion));
        }

        return dtos;
    }

    // Cambiar estado de la Prueba de conocimiento
    @Transactional
    public ApiResponse<String> cambiarEstadoPruebaAdicional(String cedula, TipoPrueba tipoPrueba, EstadoInscripcion estado) {
        InscripPruebaAdicional prueba = repoPruebaAdicional.findByTipoPruebaAndInscripcionCedula(tipoPrueba.getTipo(), cedula);

        if (estado.equals(EstadoInscripcion.Aceptado)) {
            TipoPrueba siguientePrueba = obtenerSiguientePrueba(tipoPrueba);
            if (siguientePrueba != null) {
                aprobarPruebaAdicional( prueba, tipoPrueba, LocalDate.now().plusDays(7) );
            } else {
                finalizarProcesoInscripcion(prueba.getInscripcion());
            }
        }

        prueba.setResultado(estado);
        repoPruebaAdicional.save(prueba);

        return ApiResponse.<String>builder()
                .error(false)
                .codigo(200)
                .mensaje("Prueba " + estado)
                .detalles("La prueba fue " + estado + " con éxito")
                .build();
    }

    private void crearMatricula(Inscripcion inscripcion, String paralelo) {
        repoMatricula.save(Matricula.builder()
                .inscripcion(inscripcion)
                .estado(EstadoMatricula.Matriculado)
                .fechaMatricula(java.time.LocalDate.now())
                .grado(inscripcion.getGrado())
                .curso(repoCurso.findByParaleloAndGradoNombre(paralelo, inscripcion.getGrado().getNombre()))
                .build());

    }

    /* ---------- OTROS METODOS ---------- */
    // Validar si la inscripcion a crear ya existe
    private void validarInscripcion(String cedula) {
        if (repo.existsById(cedula) || repoUsuario.existsById(cedula)) {
            throw new ApiException(ApiResponse.<String>builder()
                    .error(true)
                    .codigo(404)
                    .mensaje("Solicitud invalida")
                    .detalles("La inscripcion con la cedula " + cedula + " existe")
                    .build());
        }

    }

    // Setear documentación
    private void editCedula(Long idDoc, MultipartFile file) throws IOException {
        if (idDoc != null) {
            DocCedula cedula = repoCedula.findById(idDoc).orElseThrow(() -> new ApiException(ApiResponse.<String>builder()
                    .error(true)
                    .codigo(404)
                    .mensaje("Solicitud invalida")
                    .detalles("El documento de cedula no se ha encontrado")
                    .build()));

            cedula.setNombre(file.getOriginalFilename());
            cedula.setContenido(file.getBytes());
            cedula.setMime(file.getContentType());
            repoCedula.save(cedula);
        }
    }

    private void editCertifNotas(Long idDoc, MultipartFile file) throws IOException {
        if (idDoc != null) {
            DocCertifNota certifNotas = repoNotas.findById(idDoc).orElseThrow(() -> new ApiException(ApiResponse.<String>builder()
                    .error(true)
                    .codigo(404)
                    .mensaje("Solicitud invalida")
                    .detalles("El documento de certificado de notas no se ha encontrado")
                    .build()));

            certifNotas.setNombre(file.getOriginalFilename());
            certifNotas.setContenido(file.getBytes());
            certifNotas.setMime(file.getContentType());
            repoNotas.save(certifNotas);
        }
    }

    private void editServBasicos(Long idDoc, MultipartFile file) throws IOException {
        if (idDoc != null) {
            DocServBasicos servBasicos = repoServBasicos.findById(idDoc).orElseThrow(() -> new ApiException(ApiResponse.<String>builder()
                    .error(true)
                    .codigo(404)
                    .mensaje("Solicitud invalida")
                    .detalles("El documento de servicios basicos no se ha encontrado")
                    .build()));

            servBasicos.setNombre(file.getOriginalFilename());
            servBasicos.setContenido(file.getBytes());
            servBasicos.setMime(file.getContentType());
            repoServBasicos.save(servBasicos);
        }
    }

    // Convertir a DTO

    private List<DocumentoDTO> convertirDocumentos(Inscripcion inscripcion) {
        List<DocumentoDTO> dtos = new ArrayList<>();
        dtos.add(DocumentoDTO.builder()
                .nombre(inscripcion.getCedulaEstudiante().getNombre())
                .id(inscripcion.getCedulaEstudiante().getId())
                .mime(inscripcion.getCedulaEstudiante().getMime())
                .build()
        );
        dtos.add(DocumentoDTO.builder()
                .nombre(inscripcion.getCedulaPadre().getNombre())
                .id(inscripcion.getCedulaPadre().getId())
                .mime(inscripcion.getCedulaPadre().getMime())
                .build()
        );
        dtos.add(DocumentoDTO.builder()
                .nombre(inscripcion.getCedulaMadre().getNombre())
                .id(inscripcion.getCedulaMadre().getId())
                .mime(inscripcion.getCedulaMadre().getMime())
                .build()
        );
        dtos.add(DocumentoDTO.builder()
                .nombre(inscripcion.getCertificadoNotas().getNombre())
                .id(inscripcion.getCertificadoNotas().getId())
                .mime(inscripcion.getCertificadoNotas().getMime())
                .build()
        );
        dtos.add(DocumentoDTO.builder()
                .nombre(inscripcion.getServiciosBasicos().getNombre())
                .id(inscripcion.getServiciosBasicos().getId())
                .mime(inscripcion.getServiciosBasicos().getMime())
                .build()
        );
        return dtos;
    }

    private InscripcionDTO convertirInscripcion(Inscripcion inscripcion) {
        return InscripcionDTO.builder()
                .cedula(inscripcion.getCedula())
                .nombres(inscripcion.getNombres())
                .apellidos(inscripcion.getApellidos())
                .email(inscripcion.getEmail())
                .telefono(inscripcion.getTelefono())
                .direccion(inscripcion.getDireccion())
                .fechaNacimiento(String.valueOf(inscripcion.getFechaNacimiento()))
                .genero(String.valueOf(inscripcion.getGenero()))
                .nombresPadre(inscripcion.getNombresPadre())
                .apellidosPadre(inscripcion.getApellidosPadre())
                .correoPadre(inscripcion.getCorreoPadre())
                .telefonoPadre(inscripcion.getTelefonoPadre())
                .ocupacionPadre(inscripcion.getOcupacionPadre())
                .nombresMadre(inscripcion.getNombresMadre())
                .apellidosMadre(inscripcion.getApellidosMadre())
                .correoMadre(inscripcion.getCorreoMadre())
                .telefonoMadre(inscripcion.getTelefonoMadre())
                .ocupacionMadre(inscripcion.getOcupacionMadre())
                .estado(String.valueOf(inscripcion.getEstado()))
                .fechaInscripcion(String.valueOf(inscripcion.getFechaInscripcion()))
                .documentos(convertirDocumentos(inscripcion))
                .build();
    }

    // Prueba de Evaluación Académica
    private void generarPruebaAdicional(Inscripcion inscripcion, TipoPrueba tipoPrueba, LocalDate fechaPrueba) {
        // Guardar la prueba adicional en la base de datos
        repoPruebaAdicional.save(InscripPruebaAdicional.builder()
                .inscripcion(inscripcion)
                .tipoPrueba(tipoPrueba.getTipo())
                .descripcion(tipoPrueba.getDescripcion())
                .resultado(EstadoInscripcion.Pendiente)
                .fechaPrueba(fechaPrueba)
                .build());

        if (tipoPrueba.equals(TipoPrueba.CONOCIMIENTO)) {
            // Enviar el correo de citación a la prueba
            String destinatario = inscripcion.getRepresentante().getUsuario().getEmail();
            String asunto = "Citación a prueba de evaluación académica";
            String mensajeCorreo = mensaje.mensajeCitacionPruebaIns(
                    inscripcion.getNombres() + " " + inscripcion.getApellidos(),
                    tipoPrueba.getTipo(),
                    tipoPrueba.getDescripcion(),
                    String.valueOf(fechaPrueba)
            );

            emailService.enviarCorreo(destinatario, asunto, mensajeCorreo);
        }

    }

    private TipoPrueba obtenerSiguientePrueba(TipoPrueba tipoActual) {
        return switch (tipoActual) {
            case CONOCIMIENTO -> TipoPrueba.PSICOLOGICA;
            case PSICOLOGICA -> TipoPrueba.ENTREVISTA;
            default -> null;
        };
    }
    // Aprobación de prueba adicional y envío de mensajes
    private void aprobarPruebaAdicional(InscripPruebaAdicional prueba, TipoPrueba tipoActual, LocalDate fechaProximaPrueba) {
        TipoPrueba siguientePrueba = obtenerSiguientePrueba(tipoActual);

        String destinatario = prueba.getInscripcion().getRepresentante().getUsuario().getEmail();
        String asunto = "Resultado de la prueba: " + tipoActual.getTipo();

        if (siguientePrueba != null) {
            // Generar y enviar mensaje para la siguiente prueba
            generarPruebaAdicional(prueba.getInscripcion(), siguientePrueba, fechaProximaPrueba);

            String mensajeCorreo = mensaje.mensajeAprobacionPruebaIns(
                    prueba.getInscripcion().getNombres() + " " + prueba.getInscripcion().getApellidos(),
                    tipoActual.getTipo(),
                    tipoActual.getDescripcion(),
                    String.valueOf(LocalDate.now()),
                    siguientePrueba.getTipo(),
                    siguientePrueba.getDescripcion(),
                    String.valueOf(fechaProximaPrueba)
            );

            emailService.enviarCorreo(destinatario, asunto, mensajeCorreo);
        }
    }

    // Finalización del proceso de inscripción
    private void finalizarProcesoInscripcion(Inscripcion inscripcion) {
        TempMatricula temp = repoTempM.findByCedula(inscripcion.getCedula());

        // Crear matrícula y registrar al estudiante
        crearMatricula(inscripcion, temp.getParalelo());
        authService.registerEstudiante(inscripcion.getCedula(), Rol.ESTUDIANTE, EstadoUsu.Inactivo);

        // Enviar correo de confirmación de matrícula
        String destinatario = inscripcion.getRepresentante().getUsuario().getEmail();
        String asunto = "Inscripción completada con éxito";
        String mensajeCorreo = this.mensaje.mensajeAprobacionPruebasIns(
                inscripcion.getNombres() + " " + inscripcion.getApellidos(),
                "Entrevista Personal",
                "Conocer al estudiante y su familia para evaluar aspectos no académicos.",
                String.valueOf(LocalDate.now()),
                inscripcion.getGrado().getNombre() + " " + temp.getParalelo()
        );

        emailService.enviarCorreo(destinatario, asunto, mensajeCorreo);

        // Eliminar la inscripción de la tabla temporal
        repoTempM.delete(temp);

        inscripcion.setEstado(EstadoInscripcion.Aceptado);
        repo.save(inscripcion);
    }

    private void matricularEstudiante(Inscripcion inscripcion, String aula) {
        crearMatricula(inscripcion, aula);
        authService.registerEstudiante(inscripcion.getCedula(), Rol.ESTUDIANTE, EstadoUsu.Inactivo);
    }


}
