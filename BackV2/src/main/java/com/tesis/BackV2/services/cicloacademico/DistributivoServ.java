package com.tesis.BackV2.services.cicloacademico;

import com.tesis.BackV2.config.ApiResponse;
import com.tesis.BackV2.dto.DistributivoDTO;
import com.tesis.BackV2.entities.Curso;
import com.tesis.BackV2.entities.Distributivo;
import com.tesis.BackV2.entities.Horario;
import com.tesis.BackV2.entities.Materia;
import com.tesis.BackV2.entities.contenido.Unidad;
import com.tesis.BackV2.enums.Rol;
import com.tesis.BackV2.exceptions.ApiException;
import com.tesis.BackV2.repositories.*;
import com.tesis.BackV2.repositories.contenido.UnidadRepo;
import com.tesis.BackV2.request.DistributivoRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class DistributivoServ {
    @Autowired
    private CicloAcademicoRepo cicloRepo;
    @Autowired
    private CursoRepo cursoRepo;
    @Autowired
    private DocenteRepo docenteRepo;
    @Autowired
    private UsuarioRepo usuarioRepo;
    @Autowired
    private MateriaRepo materiaRepo;
    @Autowired
    private DistributivoRepo distributivoRepo;
    @Autowired
    private HorarioRepo horarioRepo;
    @Autowired
    private UnidadRepo unidadRepo;

    // Crear
    @Transactional
    public ApiResponse<String> crearDistributivo(DistributivoRequest request) {
        // Verificar si ya existe un distributivo con los mismos datos
        boolean existeDistributivo = distributivoRepo.findAll().stream().anyMatch(d ->
                d.getCiclo().getId() == request.getCicloId() &&
                        d.getCurso().getId() == request.getAulaId() &&
                        d.getMateria().getId() == request.getMateriaId() &&
                        d.getDocente().getId() == docenteRepo.findByUsuarioCedula(request.getCedulaDocente()).getId()
        );
        if (existeDistributivo) throw new ApiException(ApiResponse.builder()
                .error(true)
                .mensaje("Solicitud incorrecta")
                .codigo(400)
                .detalles("Ya existe un distributivo con los mismos datos")
                .build()
        );

        if (validarExistenciaAulaMateria(request)) throw new ApiException(ApiResponse.builder()
                .error(true)
                .mensaje("Soliciud incorrecta")
                .codigo(400)
                .detalles("Ya existe un distributivo con la misma aula y materia")
                .build()
        );

        // Validaciones de existencia
        validarExistenciaCicloAulaMateriaDocente(request);

        // Verificar grado de materia y aula
        validarGradoMateriaYGradoAula(request);

        Distributivo distributivo = Distributivo.builder()
                .horasAsignadas(0)
                .ciclo(cicloRepo.findById(request.getCicloId()).get())
                .curso(cursoRepo.findById(request.getAulaId()).get())
                .materia(materiaRepo.findById(request.getMateriaId()).get())
                .docente(docenteRepo.findByUsuarioCedula(request.getCedulaDocente()))
                .build();

        distributivoRepo.save(distributivo);
        return ApiResponse.<String>builder()
                .error(false)
                .mensaje("Distributivo creado")
                .codigo(200)
                .detalles("El distributivo ha sido creado correctamente")
                .build();
    }

    // Traer todos
    public List<DistributivoDTO> obtenerDistributivos() {
        return distributivoRepo.findAll().stream()
                .map(this::convertirADTO)
                .toList();
    }

    // Traer por ID
    public DistributivoDTO obtenerDistributivo(Long id) {
        Distributivo distributivo = distributivoRepo.findById(id)
                .orElseThrow(() -> new ApiException(ApiResponse.builder()
                        .error(true)
                        .mensaje("Solicitud incorrecta")
                        .codigo(400)
                        .detalles("Distributivo no encontrado")
                        .build()
                ));
        return convertirADTO(distributivo);
    }

    // Traer por curso
    public List<DistributivoDTO> getDistributivoByCurso(Long id) {
        return distributivoRepo.findByCursoId(id).stream()
                .map(this::convertirADTO)
                .toList();
    }

    // Traer por docente
    public List<DistributivoDTO> getDistributivoByDocente(String cedula) {

        List<Distributivo> distributivos = distributivoRepo.findByDocente_Usuario_Cedula(cedula);

        // Verificar si el docente tiene materias y cursos asignados
        if ( distributivos == null) {
            throw new ApiException(ApiResponse.builder()
                    .error(true)
                    .mensaje("Solicitud incorrecta")
                    .codigo(400)
                    .detalles("No cuenta con materias y cursos asignados")
                    .build()
            );
        }

        return distributivos.stream()
                .map(this::convertirADTO)
                .toList();
    }

    // Actualizar
    @Transactional
    public ApiResponse<String> editarDistributivo(DistributivoRequest request) {
        Distributivo distributivo = distributivoRepo.findById(request.getId())
                .orElseThrow(() -> new ApiException(ApiResponse.builder()
                        .error(true)
                        .mensaje("Solicitud incorrecta")
                        .codigo(400)
                        .detalles("Distributivo no encontrado")
                        .build()
                ));

        // Verificar duplicados
        boolean duplicado = distributivoRepo.findAll().stream().anyMatch(d ->
                        d.getId() != request.getId() &&
                        d.getCiclo().getId() == request.getCicloId() &&
                        d.getCurso().getId() == request.getAulaId() &&
                        d.getMateria().getId() == request.getMateriaId() &&
                        d.getDocente().getId() == docenteRepo.findByUsuarioCedula(request.getCedulaDocente()).getId()
        );
        if (duplicado) throw new ApiException(ApiResponse.builder()
                .error(true)
                .mensaje("Solcitud incorrecta")
                .codigo(400)
                .detalles("Ya existe un distributivo con los mismos datos")
                .build()
        );

        // Validaciones de existencia
        validarExistenciaCicloAulaMateriaDocente(request);

        // Verificar grado de materia y aula
        validarGradoMateriaYGradoAula(request);

        distributivo.setCiclo(cicloRepo.findById(request.getCicloId()).get());
        distributivo.setCurso(cursoRepo.findById(request.getAulaId()).get());
        distributivo.setMateria(materiaRepo.findById(request.getMateriaId()).get());
        distributivo.setDocente(docenteRepo.findByUsuarioCedula(request.getCedulaDocente()));

        distributivoRepo.save(distributivo);
        return ApiResponse.<String>builder()
                .error(false)
                .mensaje("Distributivo actualizado")
                .codigo(200)
                .detalles("El distributivo ha sido actualizado correctamente")
                .build();
    }

    // Eliminar
    @Transactional
    public ApiResponse<String> eliminarDistributivo(Long id) {
        Distributivo distributivo = distributivoRepo.findById(id)
                .orElseThrow(() -> new ApiException(ApiResponse.builder()
                        .error(true)
                        .mensaje("Solicitud incorrecta")
                        .codigo(400)
                        .detalles("Distributivo no encontrado")
                        .build()
                ));

        List<Horario> horarios = horarioRepo.findByDistributivoId(id);
        List<Unidad> unidades = unidadRepo.findByDistributivoId(id);

        // eliminar horarios en caso de existir
        if (horarios != null || unidades != null) {
            throw new ApiException(ApiResponse.builder()
                    .error(true)
                    .mensaje("Solicitud incorrecta")
                    .codigo(400)
                    .detalles("El distributivo está siendo utilizado en horarios o unidades")
                    .build()
            );
        }

        distributivoRepo.delete(distributivo);
        return ApiResponse.<String>builder()
                .error(false)
                .mensaje("Distributivo eliminado")
                .codigo(200)
                .detalles("El distributivo ha sido eliminado correctamente")
                .build();
    }

    // traer por ciclo y curso
    public List<DistributivoDTO> getDistributivoByCicloAndCurso(Long cicloId, Long cursoId) {
        return distributivoRepo.findByCicloIdAndCursoId(cicloId, cursoId).stream()
                .map(this::convertirADTO)
                .toList();
    }

    // traer por ciclo y docente
    public List<DistributivoDTO> getDistributivoByCicloAndDocente(Long cicloId, String cedula) {
        return distributivoRepo.findByCicloIdAndDocente_Usuario_Cedula(cicloId, cedula).stream()
                .map(this::convertirADTO)
                .toList();
    }

    /* --------- METODOS AUXILIARES ---------- */

    private void validarExistenciaCicloAulaMateriaDocente(DistributivoRequest request) {
        if (!cicloRepo.existsById(request.getCicloId())) throw new ApiException(ApiResponse.builder()
                .error(true)
                .mensaje("Solicitud incorrecta")
                .codigo(400)
                .detalles("El ciclo académico no existe")
                .build()
        );
        if (!cursoRepo.existsById(request.getAulaId())) throw new ApiException(ApiResponse.builder()
                .error(true)
                .mensaje("Solicitud incorrecta")
                .codigo(400)
                .detalles("El aula no existe")
                .build()
        );
        if (!materiaRepo.existsById(request.getMateriaId())) throw new ApiException(ApiResponse.builder()
                .error(true)
                .mensaje("Solicitud incorrecta")
                .codigo(400)
                .detalles("La materia no existe")
                .build()
        );
        if (!usuarioRepo.existsByCedula(request.getCedulaDocente()) ||
                !usuarioRepo.findByCedula(request.getCedulaDocente()).getRol().equals(Rol.DOCENTE)) {
            throw new ApiException(ApiResponse.builder()
                    .error(true)
                    .mensaje("Solicitud incorrecta")
                    .codigo(400)
                    .detalles("El docente no existe")
                    .build()
            );
        }
    }

    private boolean validarExistenciaAulaMateria(DistributivoRequest request) {
        return distributivoRepo.existsByCursoIdAndMateriaId(request.getAulaId(), request.getMateriaId());
    }

    private void validarGradoMateriaYGradoAula(DistributivoRequest request) {
        Long gradoMateriaId = materiaRepo.findById(request.getMateriaId()).get().getGrado().getId();
        Long gradoAulaId = cursoRepo.findById(request.getAulaId()).get().getGrado().getId();
        if (!gradoMateriaId.equals(gradoAulaId)) {
            throw new ApiException(ApiResponse.builder()
                    .error(true)
                    .mensaje("Solicitud incorrecta")
                    .codigo(400)
                    .detalles("El curso y el aula no coinciden en grado")
                    .build()
            );
        }
    }

    private DistributivoDTO convertirADTO(Distributivo distributivo) {
        return DistributivoDTO.builder()
                .id(distributivo.getId())
                .cicloAcademico(distributivo.getCiclo().getNombre())
                .aula(distributivo.getCurso().getParalelo())
                .grado(distributivo.getCurso().getGrado().getNombre())
                .materia(distributivo.getMateria().getNombre())
                .horasSemanales(distributivo.getMateria().getHoras())
                .horasAsignadas(distributivo.getHorasAsignadas())
                .docente(distributivo.getDocente().getUsuario().getNombres() + " " + distributivo.getDocente().getUsuario().getApellidos())
                .build();
    }

}
