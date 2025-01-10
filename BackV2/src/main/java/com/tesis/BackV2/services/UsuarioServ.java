package com.tesis.BackV2.services;

import com.tesis.BackV2.config.ApiResponse;
import com.tesis.BackV2.config.auth.AuthService;
import com.tesis.BackV2.dto.DocenteDTO;
import com.tesis.BackV2.dto.EstudianteDTO;
import com.tesis.BackV2.dto.RepresentanteDTO;
import com.tesis.BackV2.dto.UsuarioDTO;
import com.tesis.BackV2.entities.*;
import com.tesis.BackV2.enums.EstadoMatricula;
import com.tesis.BackV2.enums.EstadoUsu;
import com.tesis.BackV2.enums.Rol;
import com.tesis.BackV2.exceptions.ApiException;
import com.tesis.BackV2.exceptions.MiExcepcion;
import com.tesis.BackV2.repositories.*;

import com.tesis.BackV2.request.UsuarioEditRequest;
import com.tesis.BackV2.request.UsuarioRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Service
public class UsuarioServ {

    @Autowired
    private UsuarioRepo repoU;
    @Autowired
    private DocenteRepo repoD;
    @Autowired
    private EstudianteRepo repoE;
    @Autowired
    private RepresentanteRepo repoR;
    @Autowired
    private DistributivoRepo repoDist;
    @Autowired
    private CursoRepo repoC;
    @Autowired
    private MatriculaRepo repoM;
    @Autowired
    private CicloAcademicoRepo repoCA;

    @Autowired
    AuthService auth;

    /* ----- CRUD DE LA ENTIDAD USUARIO ----- */

    //Actualizar datos de un usuario (sin contar password, estado)
    @Transactional
    public ApiResponse<String> actualizarUser(UsuarioRequest request) throws MiExcepcion {
        Usuario usuario = repoU.findByCedula(request.getCedula());

        //Validar que el usuario exista
        if (usuario != null ) {
            //Actualizar los datos del usuario generales del usuario
            usuario.setNombres(request.getNombres());
            usuario.setApellidos(request.getApellidos());
            usuario.setEmail(request.getCorreo());
            usuario.setTelefono(request.getTelefono());
            usuario.setDireccion(request.getDireccion());
            usuario.setNacimiento(request.getNacimiento());
            usuario.setGenero(request.getGenero());

            repoU.save(usuario);

            switch (usuario.getRol()){
                case DOCENTE:
                    Docente docente = repoD.findByUsuarioCedula(request.getCedula());
                    docente.setTitulo(request.getTitulo());
                    docente.setEspecialidad(request.getEspecialidad());
                    docente.setExperiencia(request.getExperiencia());
                    repoD.save(docente);
                    break;
                case ESTUDIANTE:
                    Estudiante estudiante = repoE.findByUsuarioCedula(request.getCedula());
                    estudiante.setIngreso(request.getIngreso());
                    estudiante.setRepresentante(repoR.findByUsuarioCedula(request.getCedulaRepresentante()));
                    repoE.save(estudiante);
                    break;
                case REPRESENTANTE:
                    Representante representante = repoR.findByUsuarioCedula(request.getCedula());
                    representante.setAutorizado(request.isAutorizado());
                    representante.setOcupacion(request.getOcupacion());
                    representante.setEmpresa(request.getEmpresa());
                    representante.setDireccion(request.getDireccionEmpresa());
                    representante.setTelefono(request.getTelefonoEmpresa());
                    repoR.save(representante);
                    break;
            }


        } else {
            return ApiResponse.<String>builder()
                    .error(true)
                    .mensaje("Solicitud inválida")
                    .codigo(404)
                    .detalles("El usuario con cédula " + request.getCedula() + " no existe.")
                    .build();
        }

        return ApiResponse.<String>builder()
                .error(false)
                .mensaje("Usuario actualizado")
                .codigo(200)
                .detalles("El usuario ha sido actualizado correctamente.")
                .build();
    }

    // Traer usuarios matriculados en el ciclo academico activo
    public List<UsuarioDTO> estMatriculadosCicloAct() throws MiExcepcion {
        List<Matricula> matriculas = repoM.findByCicloAndEstado(repoCA.findByActivoTrue(), EstadoMatricula.Matriculado);

        List<UsuarioDTO> estudiantes = new ArrayList<>();

        for (Matricula matricula : matriculas) {
            estudiantes.add(buscarUsuario(matricula.getInscripcion().getCedula()));
        }

        return estudiantes;
    }

    public UsuarioDTO buscarUsuario(String cedula) throws MiExcepcion {

        Matricula m = repoM.findTopByInscripcionCedulaOrderByIdDesc(cedula);
        Usuario usuario = repoU.findById(cedula).orElseThrow(() -> new ApiException(
                ApiResponse.builder()
                        .error(true)
                        .mensaje("Solicitud inválida")
                        .codigo(404)
                        .detalles("El usuario con cédula " + cedula + " no existe.")
                        .build()
        ));

        Docente docente = repoD.findByUsuarioCedula(cedula);
        Estudiante estudiante = repoE.findByUsuarioCedula(cedula);
        Representante representante = repoR.findByUsuarioCedula(cedula);

        DocenteDTO docenteDTO = docente != null ? DocenteDTO.builder()
                .titulo(docente.getTitulo())
                .especialidad(docente.getEspecialidad())
                .experiencia(docente.getExperiencia())
                .build() : null;

        EstudianteDTO estudianteDTO = estudiante != null ? EstudianteDTO.builder()
                .ingreso(estudiante.getIngreso())
                .curso(m == null ? "" : m.getGrado().getNombre() + " - " + m.getCurso().getParalelo())
                .representante(RepresentanteDTO.builder()
                        .nombres(estudiante.getRepresentante().getUsuario().getNombres())
                        .apellidos(estudiante.getRepresentante().getUsuario().getApellidos())
                        .autorizado(estudiante.getRepresentante().isAutorizado())
                        .ocupacion(estudiante.getRepresentante().getOcupacion())
                        .empresa(estudiante.getRepresentante().getEmpresa())
                        .direccion(estudiante.getRepresentante().getDireccion())
                        .telefono(estudiante.getRepresentante().getTelefono())
                        .build()
                )
                .build() : null;

        RepresentanteDTO representanteDTO = representante != null ? RepresentanteDTO.builder()
                .ocupacion(representante.getOcupacion())
                .empresa(representante.getEmpresa())
                .direccion(representante.getDireccion())
                .telefono(representante.getTelefono())
                .build() : null;

        return UsuarioDTO.builder()
                .cedula(usuario.getCedula())
                .nombres(usuario.getNombres())
                .apellidos(usuario.getApellidos())
                .correo(usuario.getEmail())
                .telefono(usuario.getTelefono())
                .direccion(usuario.getDireccion())
                .nacimiento(usuario.getNacimiento())
                .genero(usuario.getGenero())
                .rol(cambiarRol(usuario.getRol()))
                .estado(usuario.getEstado())
                .docente(docenteDTO)
                .estudiante(estudianteDTO)
                .representante(representanteDTO)
                .build();

    }

    // Traer estudiantes con matriculapor medio del ciclo academico y un curso
    public List<UsuarioDTO> getEstudiantesByCurso(Long idCurso) {
        List<Matricula> matriculas = repoM.findByCicloAndCurso_Id(repoCA.findByActivoTrue(), idCurso);
        if (matriculas == null) {
            throw new ApiException(
                    ApiResponse.builder()
                            .error(true)
                            .mensaje("Error de validación")
                            .codigo(400)
                            .detalles("No hay estudiantes matriculados")
                            .build()
            );
        }

        return matriculas.stream().map(matricula -> {
            try {
                return buscarUsuario(matricula.getEstudiante().getUsuario().getCedula());
            } catch (MiExcepcion e) {
                throw new ApiException(
                        ApiResponse.builder()
                                .error(true)
                                .mensaje("Error de validación")
                                .codigo(400)
                                .detalles("No se encontró el usuario")
                                .build()
                );
            }
        }).collect(Collectors.toList());
    }

    // Traer usuarios con rol AdminGeneral, AdminOp y Docente
    public List<UsuarioDTO> getUsuarios() {
        List<Usuario> usuarios = repoU.findByRolAndCedulaNot(Rol.ADMIN, "0000099999");
        usuarios.addAll(repoU.findByRol(Rol.AOPERACIONAL));
        usuarios.addAll(repoU.findByRol(Rol.DOCENTE));

        return usuarios.stream()
                .sorted(Comparator.comparing(Usuario::getApellidos, Collator.getInstance(new Locale("es", "ES"))))
                .map(usuario -> {
                    Docente docente = repoD.findByUsuarioCedula(usuario.getCedula());
                    DocenteDTO docenteDTO = docente != null ? DocenteDTO.builder()
                            .titulo(docente.getTitulo())
                            .especialidad(docente.getEspecialidad())
                            .experiencia(docente.getExperiencia())
                            .build() : null;

                    return UsuarioDTO.builder()
                            .cedula(usuario.getCedula())
                            .nombres(usuario.getNombres())
                            .apellidos(usuario.getApellidos())
                            .correo(usuario.getEmail())
                            .telefono(usuario.getTelefono())
                            .direccion(usuario.getDireccion())
                            .nacimiento(usuario.getNacimiento())
                            .genero(usuario.getGenero())
                            .rol(cambiarRol(usuario.getRol()))
                            .estado(usuario.getEstado())
                            .docente(docenteDTO)
                            .build();
                }).collect(Collectors.toList());
    }

    // Editar el estado del usuario
    @Transactional
    public ApiResponse<String> editarEstado(UsuarioEditRequest request) {
        Usuario usuario = repoU.findById(request.getCedula()).orElseThrow(() -> new ApiException(
                ApiResponse.builder()
                        .error(true)
                        .mensaje("Solicitud inválida")
                        .codigo(404)
                        .detalles("El usuario con cédula " + request.getCedula() + " no existe.")
                        .build()
        ));

        validarEstado(String.valueOf(request.getEstado()));

        if (request.getEstado().equals("Inactivo")) {
            auth.cambiarContraUsuario(usuario);
        } else if (request.getEstado().equals("Activo")) {
            throw new ApiException(
                    ApiResponse.builder()
                            .error(true)
                            .mensaje("Error de validación")
                            .codigo(400)
                            .detalles("No se puede activar un usuario desde esta ruta.")
                            .build()
            );
        }

        usuario.setEstado(EstadoUsu.valueOf(request.getEstado()));
        repoU.save(usuario);

        return ApiResponse.<String>builder()
                .error(false)
                .mensaje("Estado actualizado")
                .codigo(200)
                .detalles("El estado del usuario ha sido actualizado correctamente.")
                .build();
    }

    // Eliminar usuario
    @Transactional
    public ApiResponse<String> eliminarUsuario(String cedula) {
        Usuario usuario = repoU.findById(cedula).orElseThrow(() -> new ApiException(
                ApiResponse.builder()
                        .error(true)
                        .mensaje("Solicitud inválida")
                        .codigo(404)
                        .detalles("El usuario con cédula " + cedula + " no existe.")
                        .build()
        ));

        validarEstado(String.valueOf(usuario.getEstado()));

        if (usuario.getRol().equals(Rol.DOCENTE)) {
            if (repoDist.existsByDocenteId(repoD.findByUsuarioCedula(cedula).getId()) && repoC.existsByTutorId(repoD.findByUsuarioCedula(cedula).getId())) {
                throw new ApiException(
                        ApiResponse.builder()
                                .error(true)
                                .mensaje("Error de validación")
                                .codigo(400)
                                .detalles("El usuario cuenta con datos relacionados")
                                .build()
                );
            } else {
                repoD.delete(repoD.findByUsuarioCedula(cedula));
            }
        }

        repoU.delete(usuario);

        return ApiResponse.<String>builder()
                .error(false)
                .mensaje("Usuario eliminado")
                .codigo(200)
                .detalles("El usuario ha sido eliminado correctamente.")
                .build();
    }

    /* ---------- VALIDACIONES ----------- */
    private void validarEstado(String estado){
        // Corroborar que el estado sea uno de los tres posibles
        if (!estado.equals("Activo") && !estado.equals("Inactivo") && !estado.equals("Suspendido")) {
            throw new ApiException(
                    ApiResponse.builder()
                            .error(true)
                            .mensaje("El estado ingresado es erroneo")
                            .codigo(400)
                            .detalles("")
                            .build()
            );
        }
    }

    private void validarRol(String rol){
        // Corroborar que el rol sea uno de los tres posibles
        if (!rol.equals("ADMIN") && !rol.equals("AOPERACIONAL") && !rol.equals("DOCENTE")) {
            throw new ApiException(
                    ApiResponse.builder()
                            .error(true)
                            .mensaje("El rol ingresado es erroneo")
                            .codigo(400)
                            .detalles("")
                            .build()
            );
        }
    }

    // cambiar rol de Est a String
    private String cambiarRol(Rol rol){
        switch (rol){
            case ADMIN:
                return "Admin";
            case AOPERACIONAL:
                return "Institucional";
            case DOCENTE:
                return "Docente";
            case ESTUDIANTE:
                return "Estudiante";
            case REPRESENTANTE:
                return "Representante";
            default:
                return "ERROR";
        }
    }

}
