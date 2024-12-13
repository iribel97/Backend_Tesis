package com.tesis.BackV2.services.inscripcion;

import com.tesis.BackV2.config.ApiResponse;
import com.tesis.BackV2.dto.MatriculaDTO;
import com.tesis.BackV2.entities.Inscripcion;
import com.tesis.BackV2.entities.Matricula;
import com.tesis.BackV2.enums.EstadoInscripcion;
import com.tesis.BackV2.enums.EstadoMatricula;
import com.tesis.BackV2.exceptions.ApiException;
import com.tesis.BackV2.repositories.*;
import com.tesis.BackV2.request.MatriculacionRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MatriculaService {

    private final MatriculaRepo repo;
    private final InscripcionRepo insRep;
    private final CursoRepo cursoRep;
    private final GradoRepo gradoRep;
    private final CicloAcademicoRepo cicloRep;

    // Crear Matricula
    public ApiResponse<String> crearMatricula(MatriculacionRequest request) {

        // Comprobar si el estudiante se encuentra inscrito
        Inscripcion inscripcion = validarInscripcion(request.getCedulaEstudiante());
        // Comprobar que la maricula existe en dicho ciclo
        if (repo.existsByInscripcionAndCiclo(inscripcion, cicloRep.findTopByOrderByIdDesc())) {
            throw new ApiException(ApiResponse.builder()
                    .error(true)
                    .mensaje("Solicitud inválida")
                    .codigo(400)
                    .detalles("El estudiante ya se encuentra matriculado en este ciclo.")
                    .build());
        }

        // Crear instancia de matricula
        Matricula matricula = Matricula.builder()
                .fechaMatricula(LocalDate.now())
                .estado(EstadoMatricula.Pendiente)
                .inscripcion(inscripcion)
                .grado(gradoRep.findByNombre(request.getGrado()))
                .ciclo(cicloRep.findTopByOrderByIdDesc())
                .build();

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
    public ApiResponse<String> actualizarMatricula(MatriculacionRequest request) {

        // Comprobar si la matricula existe
        Matricula matricula = validarMatricula(request.getId());

        Inscripcion inscripcion = validarInscripcion(request.getCedulaEstudiante());

        matricula.setCurso(cursoRep.findByParaleloAndGradoNombre(request.getParalelo(), request.getGrado()));
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
    public ApiResponse<String> eliminarMatricula(Long id){

        // Validar Matricula
        Matricula matricula = validarMatricula(id);

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
}
