package com.tesis.BackV2.services.cicloacademico;

import com.tesis.BackV2.config.ApiResponse;
import com.tesis.BackV2.dto.CursoDTO;
import com.tesis.BackV2.entities.Curso;
import com.tesis.BackV2.entities.Distributivo;
import com.tesis.BackV2.entities.Docente;
import com.tesis.BackV2.enums.Rol;
import com.tesis.BackV2.exceptions.ApiException;
import com.tesis.BackV2.repositories.*;
import com.tesis.BackV2.request.CursoRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CursoServ {

    @Autowired
    private GradoRepo gradoRepo;
    @Autowired
    private CursoRepo cursoRepo;
    @Autowired
    private DocenteRepo docenteRepo;
    @Autowired
    private UsuarioRepo usuarioRepo;
    @Autowired
    private DistributivoRepo distributivoRepo;


    // Creación
    @Transactional
    public ApiResponse<String> crearCurso(CursoRequest request) {
        validarParaleloYGrado(request.getParalelo(), request.getGrado());
        validarTutor(request.getCedulaTutor());

        Curso curso = Curso.builder()
                .paralelo(request.getParalelo())
                .maxEstudiantes(request.getCantEstudiantes())
                .grado(gradoRepo.findByNombre(request.getGrado()))
                .tutor(docenteRepo.findByUsuarioCedula(request.getCedulaTutor()))
                .build();

        cursoRepo.save(curso);
        return ApiResponse.<String>builder()
                .error(false)
                .mensaje("Aula creada")
                .codigo(200)
                .detalles("El aula ha sido creada correctamente")
                .build()
        ;
    }

    // Registrar cursos
    @Transactional
    public ApiResponse<String> registrarCursos(List<CursoRequest> request) {
        for (CursoRequest cursoRequest : request) {
            validarParaleloYGrado(cursoRequest.getParalelo(), cursoRequest.getGrado());
            validarTutor(cursoRequest.getCedulaTutor());

            Curso curso = Curso.builder()
                    .paralelo(cursoRequest.getParalelo())
                    .maxEstudiantes(cursoRequest.getCantEstudiantes())
                    .grado(gradoRepo.findByNombre(cursoRequest.getGrado()))
                    .tutor(docenteRepo.findByUsuarioCedula(cursoRequest.getCedulaTutor()))
                    .build();

            cursoRepo.save(curso);
        }
        return ApiResponse.<String>builder()
                .error(false)
                .mensaje("Aulas creadas")
                .codigo(200)
                .detalles("Las aulas han sido creadas correctamente")
                .build()
        ;
    }

    // Traer todas
    public List<CursoDTO> obtenerAulas() {
        return cursoRepo.findAll().stream()
                .map(this::convertirAulaADTO)
                .toList();
    }

    // Traer solo uno
    public CursoDTO obtenerAula(String paralelo, String grado) {
        Curso curso = cursoRepo.findByParaleloAndGradoNombre(paralelo, grado);
        if (curso == null) {
            throw new ApiException(ApiResponse.<String>builder()
                    .error(true)
                    .mensaje("Solicitud incorrecta")
                    .codigo(400)
                    .detalles("El curso no existe")
                    .build()
            );
        }
        return convertirAulaADTO(curso);
    }

    // Traer por grado
    public List<CursoDTO> obtenerAulasPorGrado(String grado) {
        return cursoRepo.findByGradoNombre(grado).stream()
                .map(this::convertirAulaADTO)
                .toList();
    }

    // Actualizar
    @Transactional
    public ApiResponse<String> editarAula(CursoRequest request) {
        Curso curso = cursoRepo.findById(request.getId())
                .orElseThrow(() -> new ApiException(ApiResponse.<String>builder()
                        .error(true)
                        .mensaje("Solcitud incorrecta")
                        .codigo(400)
                        .detalles("No es posible actualizar el curso, no existe")
                        .build()
                ));

        validarParaleloYGradoUnico(request.getParalelo(), request.getGrado(), request.getId());
        validarTutorParaEdicion(request.getCedulaTutor(), curso);

        curso.setParalelo(request.getParalelo());
        curso.setMaxEstudiantes(request.getCantEstudiantes());
        curso.setGrado(gradoRepo.findByNombre(request.getGrado()));
        curso.setTutor(docenteRepo.findByUsuarioCedula(request.getCedulaTutor()));

        cursoRepo.save(curso);
        return ApiResponse.<String>builder()
                .error(false)
                .mensaje("Aula actualizada")
                .codigo(200)
                .detalles("El aula ha sido actualizada correctamente")
                .build();
    }

    // Eliminar
    @Transactional
    public ApiResponse<String> eliminarAula(Long id) {
        Curso curso = cursoRepo.findById(id)
                .orElseThrow(() -> new ApiException(ApiResponse.<String>builder()
                        .error(true)
                        .mensaje("Solicitud incorrecta")
                        .codigo(400)
                        .detalles("El curso que intenta eliminar no existe")
                        .build()
                ));

        eliminarCursoDeDistributivo(id);

        cursoRepo.delete(curso);

        return ApiResponse.<String>builder()
                .error(false)
                .mensaje("Aula eliminada")
                .codigo(200)
                .detalles("El aula ha sido eliminada correctamente")
                .build();
    }

    /* --------- METODOS AUXILIARES ---------- */

    private void validarParaleloYGrado(String paralelo, String grado) {
        if (cursoRepo.existsByParaleloAndGradoNombre(paralelo, grado)) {
            throw new ApiException(ApiResponse.<String>builder()
                    .error(true)
                    .mensaje("Solicitud incorrecta")
                    .codigo(400)
                    .detalles("El curso ya se encuentran registrados, no se puede crear")
                    .build()
            );
        }
    }

    private void validarParaleloYGradoUnico(String paralelo, String grado, Long idActual) {
        cursoRepo.findAll().stream()
                .filter(curso -> curso.getId() != idActual)
                .filter(curso -> curso.getParalelo().equalsIgnoreCase(paralelo) &&
                        curso.getGrado().getNombre().equalsIgnoreCase(grado))
                .findFirst()
                .ifPresent(curso -> {
                    throw new ApiException(ApiResponse.<String>builder()
                            .error(true)
                            .mensaje("Solcitud incorrecta")
                            .codigo(400)
                            .detalles("El curso ya existe, no se puede actualizar")
                            .build()
                    );
                });
    }

    private void eliminarCursoDeDistributivo(Long id){
        List<Distributivo> distributivos = distributivoRepo.findByCursoId(id);

        for (Distributivo distributivo : distributivos) {
            distributivo.setCurso(null);
            distributivoRepo.save(distributivo);
        }
    }

    private void validarTutor(String cedulaTutor) {
        var usuario = usuarioRepo.findByCedula(cedulaTutor);
        if (usuario == null || !usuario.getRol().equals(Rol.DOCENTE)) {
            throw new ApiException(ApiResponse.<String>builder()
                    .error(true)
                    .mensaje("Docente no encontrado")
                    .codigo(400)
                    .detalles("La cédula ingresada no corresponde a un docente o no existe")
                    .build()
            );
        }
        if (cursoRepo.existsByTutorId(docenteRepo.findByUsuarioCedula(cedulaTutor).getId())) {
            throw new ApiException(ApiResponse.<String>builder()
                    .error(true)
                    .mensaje("Solicitud incorrecta")
                    .codigo(400)
                    .detalles("El docente ya esta asignado como tutor, no se puede asignar a otro")
                    .build()
            );
        }
    }

    private void validarTutorParaEdicion(String cedulaTutor, Curso cursoActual) {
        Docente tutor = docenteRepo.findByUsuarioCedula(cedulaTutor);
        if (tutor == null) {
            throw new ApiException(ApiResponse.<String>builder()
                    .error(true)
                    .mensaje("Solcitud incorrecta")
                    .codigo(400)
                    .detalles("El docente no existe")
                    .build()
            );
        }
        if (cursoRepo.existsByTutorId(tutor.getId()) && cursoActual.getTutor().getId() != tutor.getId()) {
            throw new ApiException(ApiResponse.<String>builder()
                    .error(true)
                    .mensaje("Solcitud incorrecta")
                    .codigo(400)
                    .detalles("Docente ya esta asignado como tutor, no se puede asignar a otro")
                    .build()
            );
        }
    }

    private CursoDTO convertirAulaADTO(Curso curso) {
        return CursoDTO.builder()
                .id(curso.getId())
                .paralelo(curso.getParalelo())
                .maxEstudiantes(curso.getMaxEstudiantes())
                .nombreGrado(curso.getGrado().getNombre())
                .tutor(curso.getTutor().getUsuario().getNombres() + " " + curso.getTutor().getUsuario().getApellidos())
                .telefonoTutor(curso.getTutor().getUsuario().getTelefono())
                .emailTutor(curso.getTutor().getUsuario().getEmail())
                .build();
    }
}
