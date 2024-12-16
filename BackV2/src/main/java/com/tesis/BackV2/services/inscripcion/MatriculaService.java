package com.tesis.BackV2.services.inscripcion;

import com.tesis.BackV2.config.ApiResponse;
import com.tesis.BackV2.config.auth.AuthService;
import com.tesis.BackV2.dto.MatriculaDTO;
import com.tesis.BackV2.entities.*;
import com.tesis.BackV2.enums.EstadoInscripcion;
import com.tesis.BackV2.enums.EstadoMatricula;
import com.tesis.BackV2.enums.EstadoUsu;
import com.tesis.BackV2.enums.Rol;
import com.tesis.BackV2.exceptions.ApiException;
import com.tesis.BackV2.infra.MensajeHtml;
import com.tesis.BackV2.repositories.*;
import com.tesis.BackV2.request.MatriculacionRequest;
import com.tesis.BackV2.services.CorreoServ;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MatriculaService {
    @Autowired
    private AuthService service;
    @Autowired
    private CorreoServ emailService;

    private final MensajeHtml mensaje = new MensajeHtml();

    private final MatriculaRepo repo;
    private final InscripcionRepo insRep;
    private final CursoRepo cursoRep;
    private final GradoRepo gradoRep;
    private final CicloAcademicoRepo cicloRep;
    private final UsuarioRepo usuRep;
    private final EstudianteRepo estRep;

    // Crear Matricula
    @Transactional
    public ApiResponse<String> crearMatricula(MatriculacionRequest request) {

        CicloAcademico ciclo = cicloRep.findTopByOrderByIdDesc();

        // Comprobar si el estudiante se encuentra inscrito
        Inscripcion inscripcion = validarInscripcion(request.getCedulaEstudiante());
        validarInscripcionAceptada(inscripcion);

        // Crear instancia de matricula
        Matricula matricula = Matricula.builder()
                .fechaMatricula(LocalDate.now())
                .estado(EstadoMatricula.Pendiente)
                .inscripcion(inscripcion)
                .grado(gradoRep.findByNombre(request.getGrado()))
                .ciclo(ciclo)
                .build();

        // Comprobar que la maricula existe en dicho ciclo
        validarMatriculaExistente(matricula);

        // Guardar matricula
        repo.save(matricula);

        return ApiResponse.<String>builder()
                .error(false)
                .mensaje("Matricula creada")
                .codigo(200)
                .detalles("Matricula creada exitosamente.")
                .build();
    }

    // Actualizar Matricula
    @Transactional
    public ApiResponse<String> actualizarMatricula(MatriculacionRequest request) {

        // Comprobar si la matricula existe
        Matricula matricula = validarMatricula(request.getId());

        Inscripcion inscripcion = validarInscripcion(request.getCedulaEstudiante());
        validarInscripcionAceptada(inscripcion);

        validarMatriculaExistente(matricula);

        if (!request.getParalelo().isEmpty()) {
            matricula.setCurso(cursoRep.findByParaleloAndGradoNombre(request.getParalelo(), request.getGrado()));
        }

        matricula.setGrado(gradoRep.findByNombre(request.getGrado()));
        matricula.setInscripcion(inscripcion);

        repo.save(matricula);

        return ApiResponse.<String>builder()
                .error(false)
                .mensaje("Matricula actualizada")
                .codigo(200)
                .detalles("Matricula actualizada exitosamente.")
                .build();
    }

    // Eliminar Matricula
    @Transactional
    public ApiResponse<String> eliminarMatricula(Long id){

        // Validar Matricula
        Matricula matricula = validarMatricula(id);

        Estudiante estudiante = estRep.findByUsuarioCedula(matricula.getInscripcion().getCedula());
        estudiante.setMatricula(null);
        estRep.save(estudiante);

        Curso curso = matricula.getCurso();
        curso.setEstudiantesAsignados(curso.getEstudiantesAsignados() - 1);
        cursoRep.save(curso);

        Usuario usu = estudiante.getUsuario();
        usu.setEstado(EstadoUsu.Suspendido);

        // Eliminar Matricula
        repo.delete(matricula);

        return ApiResponse.<String>builder()
                .error(false)
                .mensaje("Matricula eliminada")
                .codigo(200)
                .detalles("Matricula fue eliminada exitosamente.")
                .build();

    }

    // Listar por estado
    public List<MatriculaDTO> listarPorEstado(EstadoMatricula estado){
        List<Matricula> matriculas = repo.findByEstado(estado);
        return matriculas.stream().map(this::convertirDto).toList();
    }

    // Listar por representante
    public List<MatriculaDTO> listarPorRepresentante(String cedula){
        List<Matricula> matriculas = repo.findByInscripcion_Representante_Usuario_Cedula(cedula);
        return matriculas.stream().map(this::convertirDto).toList();
    }

    // Cambiar el estado de la matricula
    @Transactional
    public ApiResponse<String> cambiarEstMatricula(EstadoMatricula estado, MatriculacionRequest request){
        Matricula matricula = validarMatricula(request.getId());

        if (estado.equals(EstadoMatricula.Matriculado)){
            if (request.getParalelo().isEmpty()){
                throw new ApiException(ApiResponse.builder()
                        .error(true)
                        .mensaje("Solicitud incorrecta")
                        .codigo(400)
                        .detalles("Paralelo y grado son requeridos.")
                        .build()
                );

            }
            Curso curso = cursoRep.findByParaleloAndGradoNombre(request.getParalelo(), matricula.getGrado().getNombre());
            if (curso.getEstudiantesAsignados() == curso.getMaxEstudiantes()){
                throw new ApiException(ApiResponse.builder()
                        .error(true)
                        .mensaje("Solicitud incorrecta")
                        .codigo(400)
                        .detalles("El curso ya se encuentra lleno.")
                        .build()
                );
            }
            curso.setEstudiantesAsignados(curso.getEstudiantesAsignados() + 1);

            Usuario estudiante = traerEstudiante(matricula.getInscripcion().getCedula());

            Estudiante est = estRep.findByUsuarioCedula(estudiante.getCedula());
            est.setMatricula(matricula);
            estRep.save(est);

            matricula.setCurso(curso);
            service.cambiarContraUsuario(estudiante);
        }

        matricula.setEstado(estado);
        repo.save(matricula);

        return ApiResponse.<String>builder()
                .error(false)
                .mensaje("Cambio de estado")
                .codigo(200)
                .detalles("Matricula actualizada exitosamente.")
                .build();
    }


    /* ----------------------------- METODOS PROPIOS DEL SERVICIO ---------------------------------------*/
    private Inscripcion validarInscripcion(String cedula){
        return insRep.findById(cedula).orElseThrow(() -> new ApiException(ApiResponse.builder()
                .error(true)
                .mensaje("Solicitud inválida")
                .codigo(400)
                .detalles("Inscripción no encontrada.")
                .build()));
    }

    private Matricula validarMatricula(Long id){
        return repo.findById(id).orElseThrow(() -> new ApiException(ApiResponse.builder()
                .error(true)
                .mensaje("Solicitud inválida")
                .codigo(400)
                .detalles("Matricula no encontrada.")
                .build()));
    }

    private MatriculaDTO convertirDto(Matricula matricula){
        return MatriculaDTO.builder()
                .id(matricula.getId())
                .cedulaEstudiante(matricula.getInscripcion().getCedula())
                .nombreEstudiante(matricula.getInscripcion().getApellidos() + " " + matricula.getInscripcion().getNombres())
                .grado(matricula.getGrado().getNombre())
                .estado(matricula.getEstado().name())
                .ciclo(matricula.getCiclo().getNombre())
                .fechaMatricula(matricula.getFechaMatricula().toString())
                .build();
    }

    // Validar que la inscripción se encuentre previamente aceptada
    private void validarInscripcionAceptada(Inscripcion inscripcion){
        if(!inscripcion.getEstado().equals(EstadoInscripcion.Aceptado)){
            throw new ApiException(ApiResponse.builder()
                    .error(true)
                    .mensaje("Solicitud incorrecta")
                    .codigo(400)
                    .detalles("La inscripción no ha sido aceptada.")
                    .build()
            );
        }
    }

    // comprobar que la matricula no exista en el mismo ciclo y con la misma inscripción
    private void validarMatriculaExistente(Matricula matricula){
        if(repo.existsByInscripcionAndCiclo(matricula.getInscripcion(), matricula.getCiclo())){
            throw new ApiException(ApiResponse.builder()
                    .error(true)
                    .mensaje("Solicitud incorrecta")
                    .codigo(400)
                    .detalles("El estudiante ya se encuentra matriculado en este ciclo.")
                    .build()
            );
        }

        if (matricula.getId() != 0 && repo.existsByInscripcionAndCicloAndIdNot(matricula.getInscripcion(), matricula.getCiclo(), matricula.getId())){
            throw new ApiException(ApiResponse.builder()
                    .error(true)
                    .mensaje("Solicitud incorrecta")
                    .codigo(400)
                    .detalles("El estudiante ya se encuentra matriculado en este ciclo.")
                    .build()
            );

        }
    }

    private Usuario traerEstudiante(String cedula){
        Usuario estudiante = usuRep.findByCedula(cedula);

        if(!estudiante.getRol().equals(Rol.ESTUDIANTE)){
            throw new ApiException(ApiResponse.builder()
                    .error(true)
                    .mensaje("Solicitud incorrecta")
                    .codigo(400)
                    .detalles("Cedula incorrecta.")
                    .build()
            );
        }

        return estudiante;
    }

}
