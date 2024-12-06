package com.tesis.BackV2.services.inscripcion;

import com.tesis.BackV2.config.ApiResponse;
import com.tesis.BackV2.config.auth.AuthService;
import com.tesis.BackV2.config.auth.RegisterRequest;
import com.tesis.BackV2.entities.Inscripcion;
import com.tesis.BackV2.entities.Usuario;
import com.tesis.BackV2.entities.documentation.DocCedula;
import com.tesis.BackV2.entities.documentation.DocCertifNota;
import com.tesis.BackV2.entities.documentation.DocServBasicos;
import com.tesis.BackV2.enums.EstadoInscripcion;
import com.tesis.BackV2.enums.EstadoUsu;
import com.tesis.BackV2.enums.Rol;
import com.tesis.BackV2.exceptions.ApiException;
import com.tesis.BackV2.repositories.EstudianteRepo;
import com.tesis.BackV2.repositories.InscripcionRepo;
import com.tesis.BackV2.repositories.RepresentanteRepo;
import com.tesis.BackV2.repositories.UsuarioRepo;
import com.tesis.BackV2.repositories.documentation.DocCedulaRepo;
import com.tesis.BackV2.repositories.documentation.DocCertifNotaRepo;
import com.tesis.BackV2.repositories.documentation.DocServBasicosRepo;
import com.tesis.BackV2.request.InscripcionRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class InscripcionService {

    @Autowired
    private InscripcionRepo repo;
    @Autowired
    private DocCedulaRepo repoCedula;
    @Autowired
    private DocCertifNotaRepo repoNotas;
    @Autowired
    private DocServBasicosRepo repoServBasicos;
    @Autowired
    private RepresentanteRepo repoRepresentante;
    @Autowired
    private UsuarioRepo repoUsuario;

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

    // Editar el estado a aceptado
    public ApiResponse<String> cambiarEstadoInscripcion(String cedula, EstadoInscripcion estado) {
        Inscripcion inscripcion = repo.findById(cedula).orElseThrow(() -> new ApiException(ApiResponse.<String>builder()
                .error(true)
                .codigo(404)
                .mensaje("Solicitud invalida")
                .detalles("La inscripcion con la cedula " + cedula + " no existe")
                .build()));

        inscripcion.setEstado(estado);
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
        repoCedula.delete( inscripcion.getCedulaEstudiante() );
        repoCedula.delete( inscripcion.getCedulaPadre() );
        repoCedula.delete( inscripcion.getCedulaMadre() );
        repoNotas.delete( inscripcion.getCertificadoNotas() );
        repoServBasicos.delete( inscripcion.getServiciosBasicos() );

        repo.delete(inscripcion);
        return ApiResponse.<String>builder()
                .error(false)
                .codigo(200)
                .mensaje("Solicitud procesada")
                .detalles("La inscripcion fue eliminada con exito")
                .build();

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

}
