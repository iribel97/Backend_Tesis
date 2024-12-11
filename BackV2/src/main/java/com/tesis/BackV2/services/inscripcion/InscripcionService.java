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
import com.tesis.BackV2.enums.EstadoInscripcion;
import com.tesis.BackV2.enums.EstadoMatricula;
import com.tesis.BackV2.enums.EstadoUsu;
import com.tesis.BackV2.enums.Rol;
import com.tesis.BackV2.exceptions.ApiException;
import com.tesis.BackV2.infra.MensajeHtml;
import com.tesis.BackV2.repositories.*;
import com.tesis.BackV2.repositories.documentation.DocCedulaRepo;
import com.tesis.BackV2.repositories.documentation.DocCertifNotaRepo;
import com.tesis.BackV2.repositories.documentation.DocServBasicosRepo;
import com.tesis.BackV2.repositories.documentation.InscripPruebaAdicionalRepo;
import com.tesis.BackV2.request.InscripcionRequest;
import com.tesis.BackV2.request.MatriculacionRequest;
import com.tesis.BackV2.services.CorreoServ;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
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

            Inscripcion inscripcion = Inscripcion.builder()
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
                    .cedulaEstudiante(repoCedula.save(DocCedula.builder()
                            .nombre(cedulaEstudiante.getOriginalFilename())
                            .contenido(cedulaEstudiante.getBytes())
                            .mime(cedulaEstudiante.getContentType())
                            .build()))
                    .cedulaPadre(repoCedula.save(DocCedula.builder()
                            .nombre(cedulaPadre.getOriginalFilename())
                            .contenido(cedulaPadre.getBytes())
                            .mime(cedulaPadre.getContentType())
                            .build()))
                    .cedulaMadre(repoCedula.save(DocCedula.builder()
                            .nombre(cedulaMadre.getOriginalFilename())
                            .contenido(cedulaMadre.getBytes())
                            .mime(cedulaMadre.getContentType())
                            .build()))
                    .certificadoNotas(repoNotas.save(DocCertifNota.builder()
                            .nombre(certificadoNotas.getOriginalFilename())
                            .contenido(certificadoNotas.getBytes())
                            .mime(certificadoNotas.getContentType())
                            .build()))
                    .serviciosBasicos(repoServBasicos.save(DocServBasicos.builder()
                            .nombre(serviciosBasicos.getOriginalFilename())
                            .contenido(serviciosBasicos.getBytes())
                            .mime(serviciosBasicos.getContentType())
                            .build()))
                    .representante(repoRepresentante.findByUsuarioCedula(request.getRepresentanteId()))
                    .build();

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
                .mensaje("Inscripcion creada")
                .detalles("La inscripcion fue creada con exito")
                .build();
    }

    // Editar la inscripcion
    public ApiResponse<String> editarInscripcion(InscripcionRequest request,
                                                 MultipartFile cedulaEstudiante,
                                                 MultipartFile cedulaPadre,
                                                 MultipartFile cedulaMadre,
                                                 MultipartFile certificadoNotas,
                                                 MultipartFile serviciosBasicos) throws IOException {
        try{

            Inscripcion inscripcion = repo.findById(request.getCedula()).orElseThrow(() -> new ApiException(ApiResponse.<String>builder()
                    .error(true)
                    .codigo(404)
                    .mensaje("Solicitud invalida")
                    .detalles("La inscripcion con la cedula " + request.getCedula() + " no existe")
                    .build()));

            if (inscripcion.getEstado().equals(EstadoInscripcion.Aceptado)) {
                return ApiResponse.<String>builder()
                        .error(true)
                        .codigo(400)
                        .mensaje("Error al editar la inscripcion")
                        .detalles("La inscripcion ya fue aceptada")
                        .build();
            }

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
            repo.save(inscripcion);

            editCedula(inscripcion.getCedulaEstudiante().getId(), cedulaEstudiante);
            editCedula(inscripcion.getCedulaPadre().getId(), cedulaPadre);
            editCedula(inscripcion.getCedulaMadre().getId(), cedulaMadre);
            editCertifNotas(inscripcion.getCertificadoNotas().getId(), certificadoNotas);
            editServBasicos(inscripcion.getServiciosBasicos().getId(), serviciosBasicos);

            return ApiResponse.<String>builder()
                    .error(false)
                    .codigo(200)
                    .mensaje("Inscripcion editada")
                    .detalles("La inscripcion fue editada con exito")
                    .build();

        }catch (IOException e) {
            return ApiResponse.<String>builder()
                    .error(true)
                    .codigo(400)
                    .mensaje("Error al guardar los documentos")
                    .detalles(e.getMessage())
                    .build();
        }
    }

    // Editar el estado
    public ApiResponse<String> cambiarEstadoInscripcion(String cedula, EstadoInscripcion estado) {
        Inscripcion inscripcion = repo.findById(cedula).orElseThrow(() -> new ApiException(ApiResponse.<String>builder()
                .error(true)
                .codigo(404)
                .mensaje("Solicitud invalida")
                .detalles("La inscripcion con la cedula " + cedula + " no existe")
                .build()));

        // Si el estado es aceptado y requiere pruebas
        if (estado.equals(EstadoInscripcion.Aceptado) && inscripcion.getCilo().getInscripConfig().isRequierePruebas()) {
            // Crear prueba de conocimiento
            crearPruebaAdicional(cedula,
                    "Prueba de conocimiento",
                    "Evaluar los conocimientos del estudiante en áreas clave (matemáticas, lengua, ciencias)",
                    java.time.LocalDate.now().plusDays(7),"","",null);

            // Cambiar estado a prueba
            inscripcion.setEstado(EstadoInscripcion.Prueba);
        } else {
            inscripcion.setEstado(estado);
        }

        repo.save(inscripcion);

        return ApiResponse.<String>builder()
                .error(false)
                .codigo(200)
                .mensaje("Inscripcion " + estado)
                .detalles("La inscripcion fue " + estado + " con exito")
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
    public ApiResponse<String> cambiarEstadoPruebaAdicional(Long id,String tipoPrueba, EstadoInscripcion estado, MatriculacionRequest request) {
        InscripPruebaAdicional prueba = repoPruebaAdicional.findByTipoPruebaAndInscripcionCedula(tipoPrueba, String.valueOf(id));

        if (estado.equals(EstadoInscripcion.Aceptado)) {
            switch (prueba.getTipoPrueba()) {
                case "Prueba de conocimiento":

                    crearPruebaAdicional(prueba.getInscripcion().getCedula(),
                            "Prueba Psicológica",
                            "Evaluar aspectos emocionales, sociales, y habilidades cognitivas del estudiante.",
                            java.time.LocalDate.now().plusDays(7),prueba.getTipoPrueba(),prueba.getDescripcion(),prueba.getFechaPrueba());
                    break;
                case "Prueba Psicológica":
                    crearPruebaAdicional(prueba.getInscripcion().getCedula(),
                            "Entrevista Personal",
                            "Conocer al estudiante y su familia para evaluar aspectos no académicos.",
                            java.time.LocalDate.now().plusDays(7),prueba.getTipoPrueba(),prueba.getDescripcion(),prueba.getFechaPrueba());
                    break;
                case "Entrevista Personal":
                    String estudiante = prueba.getInscripcion().getNombres() + " " + prueba.getInscripcion().getApellidos();
                    crearMatricula(prueba.getInscripcion(), request);
                    String destinatario = prueba.getInscripcion().getRepresentante().getUsuario().getEmail();
                    String asunto = "Citación a prueba de evaluación académica";

                    try {
                        emailService.enviarCorreo(destinatario, asunto, mensaje.mensajeAprobacionPruebasIns(estudiante, prueba.getTipoPrueba(), prueba.getDescripcion(), String.valueOf(LocalDate.now()), request.getGrado() + " " + request.getParalelo()));
                    } catch (Exception e) {

                        throw new ApiException(ApiResponse.builder()
                                .error(true)
                                .mensaje("Error al enviar el correo.")
                                .codigo(500)
                                .detalles(e.getMessage())
                                .build()
                        );
                    }

                    crearMatricula(prueba.getInscripcion(), request);

                    // Crear estudiante
                    authService.registerEstudiante(prueba.getInscripcion().getCedula(), Rol.ESTUDIANTE, EstadoUsu.Inactivo);
                    break;
            }
        }

        prueba.setResultado(estado);
        repoPruebaAdicional.save(prueba);

        return ApiResponse.<String>builder()
                .error(false)
                .codigo(200)
                .mensaje("Prueba " + estado)
                .detalles("La prueba fue " + estado + " con exito")
                .build();
    }

    private void crearMatricula(Inscripcion inscripcion, MatriculacionRequest request) {
        repoMatricula.save(Matricula.builder()
                .inscripcion(inscripcion)
                .estado(EstadoMatricula.Matriculado)
                .fechaMatricula(java.time.LocalDate.now())
                .grado(inscripcion.getGrado())
                .curso(repoCurso.findByParaleloAndGradoNombre(request.getParalelo(), inscripcion.getGrado().getNombre()))
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

    // Prueba de Evaluación académica
    private void crearPruebaAdicional(String idInscrip, String tipoPrueba, String descripcion, java.time.LocalDate fechaPrueba, String tipoPruebaAnt, String descripcionAnt, java.time.LocalDate fechaPruebaAnt) {
        String mensaje = "";

        Inscripcion inscripcion = repo.findById(idInscrip).orElseThrow(() -> new ApiException(ApiResponse.<String>builder()
                .error(true)
                .codigo(404)
                .mensaje("Solicitud invalida")
                .detalles("La inscripcion con la cedula " + idInscrip + " no existe")
                .build()));

        repoPruebaAdicional.save(InscripPruebaAdicional.builder()
                .tipoPrueba(tipoPrueba)
                .descripcion(descripcion)
                .resultado(EstadoInscripcion.Pendiente)
                .fechaPrueba(fechaPrueba)
                .inscripcion(inscripcion)
                .build());

        String estudiante = inscripcion.getNombres() + " " + inscripcion.getApellidos();

        if (tipoPrueba.equalsIgnoreCase("Prueba de conocimiento")) {
            mensaje = this.mensaje.mensajeCitacionPruebaIns(estudiante, tipoPrueba, descripcion, String.valueOf(fechaPrueba));
        } else if (tipoPrueba.equalsIgnoreCase("Entrevista Personal")) {
            mensaje = this.mensaje.mensajeAprobacionPruebaIns(estudiante, tipoPruebaAnt, descripcionAnt, String.valueOf(fechaPruebaAnt), tipoPrueba, descripcion, String.valueOf(fechaPrueba));
        }

        String destinatario = inscripcion.getRepresentante().getUsuario().getEmail();
        String asunto = "Citación a prueba de evaluación académica";

        try {
            emailService.enviarCorreo(destinatario, asunto, mensaje);
        } catch (Exception e) {

            throw new ApiException(ApiResponse.builder()
                    .error(true)
                    .mensaje("Error al enviar el correo.")
                    .codigo(500)
                    .detalles(e.getMessage())
                    .build()
            );
        }
    }

}
