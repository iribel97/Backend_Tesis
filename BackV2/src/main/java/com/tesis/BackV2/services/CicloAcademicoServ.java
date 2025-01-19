package com.tesis.BackV2.services;

import com.tesis.BackV2.config.ApiResponse;
import com.tesis.BackV2.dto.*;
import com.tesis.BackV2.dto.cicloAcademico.CicloDTO;
import com.tesis.BackV2.dto.dashboard.CantEstCursoDTO;
import com.tesis.BackV2.dto.dashboard.CantidadesDTO;
import com.tesis.BackV2.dto.horarioConfig.DiaDTO;
import com.tesis.BackV2.dto.horarioConfig.HoraDTO;
import com.tesis.BackV2.entities.*;
import com.tesis.BackV2.entities.config.HorarioConfig;
import com.tesis.BackV2.entities.config.InscripcionConfig;
import com.tesis.BackV2.entities.contenido.Unidad;
import com.tesis.BackV2.enums.Rol;
import com.tesis.BackV2.exceptions.ApiException;
import com.tesis.BackV2.repositories.*;
import com.tesis.BackV2.repositories.config.HorarioConfigRepo;
import com.tesis.BackV2.repositories.config.InscripcionConfigRepo;
import com.tesis.BackV2.repositories.contenido.UnidadRepo;
import com.tesis.BackV2.request.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.Normalizer;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CicloAcademicoServ {

    private final CicloAcademicoRepo cicloRepo;
    private final CursoRepo cursoRepo;
    private final DistributivoRepo distributivoRepo;
    private final DocenteRepo docenteRepo;
    private final GradoRepo gradoRepo;
    private final HorarioRepo horarioRepo;
    private final HorarioConfigRepo horarioConfigRepo;
    private final InscripcionConfigRepo insConfigRepo;
    private final MateriaRepo materiaRepo;
    private final PromocionRepo promocionRepo;
    private final UnidadRepo unidadRepo;
    private final UsuarioRepo usuarioRepo;






    /* ---------------------------- Ciclo Académico  ---------------------------- */
    // Creación
    @Transactional
    public ApiResponse<String> crearCicloAcademico(CicloARequest request) {
        boolean existeConflicto = cicloRepo.findAll().stream()
                .anyMatch(ciclo ->
                        request.getFechaInicio().isBefore(ciclo.getFechaFin()) &&
                                request.getFechaFin().isAfter(ciclo.getFechaInicio())
                );

        if (existeConflicto) {
            throw new ApiException(ApiResponse.builder()
                    .error(true)
                    .mensaje("Conflicto de fechas")
                    .codigo(400)
                    .detalles("El ciclo académico tiene conflicto de fechas con otro ciclo académico.")
                    .build()
            );
        }

        CicloAcademico ciclo = CicloAcademico.builder()
                .nombre(request.getNombre())
                .cantPeriodos(request.getCantPeriodos())
                .fechaInicio(request.getFechaInicio())
                .fechaFin(request.getFechaFin())
                .inscripConfig(insConfigRepo.save(InscripcionConfig.builder()
                        .requierePruebas(request.isPruebaInscrip())
                        .fechaConfiguracion(request.getFechaInicio().atStartOfDay())
                        .build()))
                .activo(false)
                .build();

        cicloRepo.save(ciclo);

        // Crear Promoción
        Promocion prom = Promocion.builder()
                .cicloAcademico(ciclo)
                .nombre("Promoción " + convertirARomano(promocionRepo.findAll().size() + 1))
                .grado(gradoRepo.findTopByOrderByIdAsc() != null ? gradoRepo.findTopByOrderByIdAsc() : null)
                .build();

        promocionRepo.save(prom);

        // Actualizar grados para las promociones existentes
        List<Promocion> promociones = promocionRepo.findAll();
        List<Grado> grados = gradoRepo.findAll();

        int minTamano = Math.min(promociones.size(), grados.size());

        for (int i = 0; i < minTamano; i++) {
            promociones.get(promociones.size()-1-i).setGrado(grados.get(i));
            promocionRepo.save(promociones.get(promociones.size()-1-i));
        }

        return ApiResponse.<String>builder()
                .error(false)
                .mensaje("Ciclo académico creado")
                .codigo(200)
                .detalles("El ciclo académico ha sido creado correctamente.")
                .build();
    }

    // Traer todos
    public List<CicloDTO> getCiclos() {
        return cicloRepo.findAll().stream()
                .map(this::convertirCicloDTO)
                .toList();
    }

    // Traer un solo por id
    public CicloDTO getCiclo(Long id) {
        return convertirCicloDTO(
                cicloRepo.findById(id).orElseThrow(() -> new ApiException(
                        ApiResponse.<String>builder()
                                .error(true)
                                .mensaje("Solicitud incorrecta")
                                .codigo(400)
                                .detalles("El ciclo académico no existe")
                                .build()
                ))
        );
    }
    // traer ciclo activo
    public CicloDTO getCicloActivo() {
        return convertirCicloDTO(cicloRepo.findByActivoTrue());
    }

    // Editar
    @Transactional
    public ApiResponse<String> editarCiclo(CicloARequest request) {
        CicloAcademico ciclo = cicloRepo.findById(request.getId())
                .orElseThrow(() -> new ApiException(ApiResponse.builder()
                        .error(true)
                        .mensaje("Solicitud incorrecta")
                        .codigo(400)
                        .detalles("El ciclo académico no existe.")
                        .build()
                ));

        // Validar si se está activando el ciclo
        if (request.isActivo()) {
            // Buscar el ciclo actualmente activo
            CicloAcademico cicloActivo = cicloRepo.findByActivoTrue();

            if (request.getId() != cicloActivo.getId()) {
                // Verificar que las fechas no estén dentro del rango del ciclo activo
                if (!LocalDate.now().isAfter(cicloActivo.getFechaFin())) {
                    throw new ApiException(ApiResponse.builder()
                            .error(true)
                            .mensaje("Solicitud invalida")
                            .codigo(400)
                            .detalles("No se puede activar un nuevo ciclo académico dentro del rango del ciclo activo actual.")
                            .build());
                }
                // Desactivar el ciclo actualmente activo
                cicloActivo.setActivo(false);
                cicloRepo.save(cicloActivo);
            }
        }

        ciclo.setNombre(request.getNombre());
        ciclo.setCantPeriodos(request.getCantPeriodos());
        ciclo.setFechaInicio(request.getFechaInicio());
        ciclo.setFechaFin(request.getFechaFin());
        ciclo.setActivo(ciclo.isActivo() || request.isActivo());

        cicloRepo.save(ciclo);
        return ApiResponse.<String>builder()
                .error(false)
                .mensaje("Ciclo académico actualizado")
                .codigo(200)
                .detalles("El ciclo académico ha sido actualizado correctamente.")
                .build();
    }

    // Eliminar
    @Transactional
    public ApiResponse<String> eliminarCiclo(Long id) {
        CicloAcademico ciclo = cicloRepo.findById(id).orElseThrow(() -> new RuntimeException("Ciclo académico no encontrado."));

        if (distributivoRepo.existsByCicloId(id)) {
            throw new ApiException(ApiResponse.builder()
                    .error(true)
                    .mensaje("Solicitud incorrecta")
                    .codigo(400)
                    .detalles("No es posible eliminar el ciclo académico, tiene distributivos asociados.")
                    .build()
            );
        }

        cicloRepo.delete(ciclo);
        return ApiResponse.<String>builder()
                .error(false)
                .mensaje("Ciclo académico eliminado")
                .codigo(200)
                .detalles("El ciclo académico ha sido eliminado correctamente.")
                .build();
    }

    private CicloDTO convertirCicloDTO(CicloAcademico ciclo){
        return CicloDTO.builder()
                .id(ciclo.getId())
                .nombre(ciclo.getNombre())
                .cantPeriodos(ciclo.getCantPeriodos())
                .fechaInicio(ciclo.getFechaInicio())
                .fechaFin(ciclo.getFechaFin())
                .activo(ciclo.isActivo())
                .requierePruebas(ciclo.getInscripConfig().isRequierePruebas())
                .build();
    }

    private String convertirARomano(int numero) {

        String[] miles = {"", "M", "MM", "MMM"};
        String[] cientos = {"", "C", "CC", "CCC", "CD", "D", "DC", "DCC", "DCCC", "CM"};
        String[] decenas = {"", "X", "XX", "XXX", "XL", "L", "LX", "LXX", "LXXX", "XC"};
        String[] unidades = {"", "I", "II", "III", "IV", "V", "VI", "VII", "VIII", "IX"};

        return miles[numero / 1000] +
                cientos[(numero % 1000) / 100] +
                decenas[(numero % 100) / 10] +
                unidades[numero % 10];
    }

    /* -------------------------- CURSOS ------------------------------------ */
    // Creación
    @Transactional
    public ApiResponse<String> crearCurso(CursoRequest request) {
        validarParaleloYGrado(request.getParalelo(), request.getGrado());
        validarTutor(request.getTutorId());

        Curso curso = Curso.builder()
                .paralelo(request.getParalelo())
                .maxEstudiantes(request.getCantEstudiantes())
                .grado(gradoRepo.findByNombre(request.getGrado()))
                .tutor(docenteRepo.findById(request.getTutorId()).get())
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

    // Traer todas
    public List<CursoDTO> obtenerAulas() {
        return cursoRepo.findAll().stream()
                .map(this::convertirAulaADTO)
                .toList();
    }

    // Traer por grado
    public List<CursoDTO> obtenerAulasPorGrado(String grado) {
        return cursoRepo.findByGradoNombre(grado).stream()
                .map(this::convertirAulaADTO)
                .toList();
    }

    // Mostrar cantidad de estudiantes por curso
public List<CantEstCursoDTO> obtenerCantidadesEstudiantes() {
    List<Curso> cursos = cursoRepo.findAll();
    Map<String, CantEstCursoDTO> cantidadesPorGrado = new HashMap<>();

    for (Curso curso : cursos) {
        String nombreGrado = curso.getGrado().getNombre();
        CantEstCursoDTO cantidades = cantidadesPorGrado.getOrDefault(nombreGrado, new CantEstCursoDTO(0, 0));

        cantidades.setTotalEstudiantes(cantidades.getTotalEstudiantes() + curso.getMaxEstudiantes());
        cantidades.setAsigEstudiantes(cantidades.getAsigEstudiantes() + curso.getEstudiantesAsignados());

        cantidadesPorGrado.put(nombreGrado, cantidades);
    }

    return new ArrayList<>(cantidadesPorGrado.values());
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
        validarTutorParaEdicion(request.getTutorId(), curso);

        curso.setParalelo(request.getParalelo());
        curso.setMaxEstudiantes(request.getCantEstudiantes());
        curso.setGrado(gradoRepo.findByNombre(request.getGrado()));
        curso.setTutor(docenteRepo.findById(request.getTutorId()).get());

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

    private void validarTutor(long tutorId) {
        var usuario = docenteRepo.findById(tutorId).orElseThrow(() -> new ApiException(ApiResponse.<String>builder()
                .error(true)
                .mensaje("Solicitud incorrecta")
                .codigo(400)
                .detalles("Docente no encontrado")
                .build()
        ));
        if (cursoRepo.existsByTutorId(tutorId)) {
            throw new ApiException(ApiResponse.<String>builder()
                    .error(true)
                    .mensaje("Solicitud incorrecta")
                    .codigo(400)
                    .detalles("El docente ya esta asignado como tutor, no se puede asignar a otro")
                    .build()
            );
        }
    }

    private void validarTutorParaEdicion(long idTutor, Curso cursoActual) {
        Docente tutor = docenteRepo.findById(idTutor).orElseThrow(() -> new ApiException(ApiResponse.<String>builder()
                .error(true)
                .mensaje("Solicitud incorrecta")
                .codigo(400)
                .detalles("Docente no encontrado")
                .build()
        ));
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
                .cant(curso.getEstudiantesAsignados())
                .nombreGrado(curso.getGrado().getNombre())
                .tutor(curso.getTutor().getUsuario().getNombres() + " " + curso.getTutor().getUsuario().getApellidos())
                .telefonoTutor(curso.getTutor().getUsuario().getTelefono())
                .emailTutor(curso.getTutor().getUsuario().getEmail())
                .build();
    }

    /* -------------------------- GRADO ------------------------------------ */
    // Creación ejem: octavo, noveno, decimo
    @Transactional
    public ApiResponse<String> crearGrado(Grado request) {
        boolean gradoExiste = gradoRepo.existsByNombreIgnoreCase(request.getNombre());
        if (gradoExiste) {
            throw new ApiException(ApiResponse.builder()
                    .error(true)
                    .mensaje("Solicitud inválida")
                    .codigo(400)
                    .detalles("El grado académico ya existe.")
                    .build()
            );
        }

        Grado grado = Grado.builder()
                .nombre(request.getNombre())
                .build();

        gradoRepo.save(grado);
        return ApiResponse.<String>builder()
                .error(false)
                .mensaje("Grado académico creado")
                .codigo(200)
                .detalles("El grado académico ha sido creado correctamente.")
                .build();
    }

    // Traer todos
    public List<SelectDTO> getGrados() {
        List<Grado> grados = gradoRepo.findAll();

        return grados.stream()
                .map(grado -> SelectDTO.builder()
                        .id(grado.getId())
                        .name(grado.getNombre())
                        .build()
                )
                .toList();
    }

    // Traer por nombre
    public Grado getGrado(String nombre) {
        Grado grado = gradoRepo.findByNombre(nombre);
        if (grado == null) {
            throw new ApiException(ApiResponse.builder()
                    .error(true)
                    .mensaje("Solicitud inválida")
                    .codigo(400)
                    .detalles("El grado académico no existe.")
                    .build()
            );
        }
        return grado;
    }

    // Acualizar
    @Transactional
    public ApiResponse<String> editarGrado(Grado request) {
        Grado grado = gradoRepo.findById(request.getId())
                .orElseThrow(() -> new ApiException(ApiResponse.builder()
                        .error(true)
                        .mensaje("Solicitud inválida")
                        .codigo(400)
                        .detalles("El grado académico no existe.")
                        .build()
                ));

        boolean nombreEnUso = gradoRepo.existsByNombreIgnoreCaseAndIdNot(request.getNombre(), request.getId());
        if (nombreEnUso) {
            throw new ApiException(ApiResponse.builder()
                    .error(true)
                    .mensaje("Solicitud inválida")
                    .codigo(400)
                    .detalles("El nombre del grado académico ya está en uso.")
                    .build()
            );
        }

        grado.setNombre(request.getNombre());
        gradoRepo.save(grado);

        return ApiResponse.<String>builder()
                .error(false)
                .mensaje("Grado académico actualizado")
                .codigo(200)
                .detalles("El grado académico ha sido actualizado correctamente.")
                .build();
    }

    // Eliminar
    @Transactional
    public ApiResponse<String> eliminarGrado(Long id) {
        Grado grado = gradoRepo.findById(id).orElseThrow(() -> new ApiException(ApiResponse.builder()
                .error(true)
                .mensaje("Solicitud inválida")
                .codigo(400)
                .detalles("El grado académico no existe.")
                .build()
        ));

        if (cursoRepo.existsByGradoId(id) || materiaRepo.existsByGradoId(id)) {
            throw new ApiException(ApiResponse.builder()
                    .error(true)
                    .mensaje("Solicitud inválida")
                    .codigo(400)
                    .detalles("El grado académico está asociado a un aula o materia.")
                    .build()
            );
        }

        gradoRepo.delete(grado);
        return ApiResponse.<String>builder()
                .error(false)
                .mensaje("Grado académico eliminado")
                .codigo(200)
                .detalles("El grado académico ha sido eliminado correctamente.")
                .build();
    }

    /* -------------------------- MATERIA ------------------------------------ */
    // Crear
    @Transactional
    public ApiResponse<String> crearMateria(MateriaRequest request) {
        var grado = gradoRepo.findByNombre(request.getGrado());
        if (grado == null) {
            throw new ApiException(ApiResponse.builder()
                    .error(true)
                    .mensaje("Solicitud inválida")
                    .codigo(404)
                    .detalles("El grado especificado no existe.")
                    .build()
            );
        }

        boolean materiaExiste = materiaRepo.existsByNombreAndGradoNombre(request.getNombre(), request.getGrado());
        if (materiaExiste) {
            throw new ApiException(ApiResponse.builder()
                    .error(true)
                    .mensaje("Solicitud inválida")
                    .codigo(400)
                    .detalles("La materia ya existe en el grado especificado.")
                    .build()
            );
        }

        Materia materia = Materia.builder()
                .nombre(request.getNombre())
                .horas(request.getHoras())
                .area(request.getArea())
                .grado(grado)
                .registroCalificacion(request.getRegistroCalificacion())
                .build();

        materiaRepo.save(materia);
        return ApiResponse.<String>builder()
                .error(false)
                .mensaje("Materia creada")
                .codigo(200)
                .detalles("La materia ha sido creada correctamente.")
                .build();
    }

    // Obtener todas
    public List<MateriaDTO> getMaterias() {
        List<Materia> materias = materiaRepo.findAll();
        List<MateriaDTO> materiasDTO = new ArrayList<>();
        materias.forEach(materia -> materiasDTO.add(convertirMateriaADTO(materia)));
        return materiasDTO;
    }

    // Obtener una
    public MateriaDTO getMateria(long id) {
        Materia materia = materiaRepo.findById(id)
                .orElseThrow(() -> new ApiException(ApiResponse.builder()
                        .error(true)
                        .mensaje("Solicitud inválida")
                        .codigo(400)
                        .detalles("Materia no encontrada.")
                        .build()
                ));
        return convertirMateriaADTO(materia);
    }

    // Traer por grado
    public List<MateriaDTO> getMateriasPorGrado(String grado) {
        List<Materia> materias = materiaRepo.findByGradoNombre(grado);
        return materias.stream()
                .map(this::convertirMateriaADTO)
                .collect(Collectors.toList());
    }

    // Actualizar
    @Transactional
    public ApiResponse<String> editarMateria(MateriaRequest request) {
        Materia materia = materiaRepo.findById(request.getId())
                .orElseThrow(() -> new ApiException(ApiResponse.builder()
                        .error(true)
                        .mensaje("Solicitud inválida")
                        .codigo(400)
                        .detalles("Materia no encontrada.")
                        .build()
                ));

        var grado = gradoRepo.findByNombre(request.getGrado());
        if (grado == null) {
            throw new ApiException(ApiResponse.builder()
                    .error(true)
                    .mensaje("Solicitud inválida")
                    .codigo(404)
                    .detalles("El grado especificado no existe.")
                    .build()
            );
        }

        boolean materiaExiste = materiaRepo.existsByNombreAndGradoNombre(request.getNombre(), request.getGrado())
                && !materia.getNombre().equalsIgnoreCase(request.getNombre());
        if (materiaExiste) {
            throw new ApiException(ApiResponse.builder()
                    .error(true)
                    .mensaje("Solicitud inválida")
                    .codigo(400)
                    .detalles("La materia ya existe en el grado especificado.")
                    .build()
            );
        }

        materia.setNombre(request.getNombre());
        materia.setHoras(request.getHoras());
        materia.setArea(request.getArea());
        materia.setGrado(grado);

        materiaRepo.save(materia);
        return ApiResponse.<String>builder()
                .error(false)
                .mensaje("Materia actualizada")
                .codigo(200)
                .detalles("La materia ha sido actualizada correctamente.")
                .build();
    }

    // Eliminar
    @Transactional
    public ApiResponse<String> eliminarMateria(Long id) {
        Materia materia = materiaRepo.findById(id)
                .orElseThrow(() -> new ApiException(ApiResponse.builder()
                        .error(true)
                        .mensaje("Solicitud inválida")
                        .codigo(400)
                        .detalles("Materia no encontrada.")
                        .build()
                ));

        if (distributivoRepo.existsByMateriaId(id)) {
            throw new ApiException(ApiResponse.builder()
                    .error(true)
                    .mensaje("Solicitud inválida")
                    .codigo(400)
                    .detalles("La materia está siendo utilizada en un distributivo.")
                    .build()
            );
        }

        materiaRepo.delete(materia);
        return ApiResponse.<String>builder()
                .error(false)
                .mensaje("Materia eliminada")
                .codigo(200)
                .detalles("La materia ha sido eliminada correctamente.")
                .build();
    }

    // Convertir a DTO
    private MateriaDTO convertirMateriaADTO(Materia materia) {
        return MateriaDTO.builder()
                .id(materia.getId())
                .nombre(materia.getNombre())
                .area(materia.getArea())
                .horasSemanales(materia.getHoras())
                .nombreGrado(materia.getGrado().getNombre())
                .build();
    }

    /* -------------------------- DISTRIBUTIVO ------------------------- */
    // Crear
    @Transactional
    public ApiResponse<String> crearDistributivo(DistributivoRequest request) {
        // Verificar si ya existe un distributivo con los mismos datos
        boolean existeDistributivo = distributivoRepo.findAll().stream().anyMatch(d ->
                d.getCiclo().getId() == request.getCicloId() &&
                        d.getCurso().getId() == request.getAulaId() &&
                        d.getMateria().getId() == request.getMateriaId() &&
                        d.getDocente().getId() == docenteRepo.findById(request.getIdDocente()).orElseThrow( () -> new ApiException(ApiResponse.builder()
                                .error(true)
                                .mensaje("Solicitud incorrecta")
                                .codigo(400)
                                .detalles("Docente no encontrado")
                                .build()
                        )).getId()
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
                .docente(docenteRepo.findById(request.getIdDocente()).get())
                .build();

        distributivoRepo.save(distributivo);
        return ApiResponse.<String>builder()
                .error(false)
                .mensaje("Distributivo creado")
                .codigo(200)
                .detalles("El distributivo ha sido creado correctamente")
                .build();
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
        return convertirDisADTO(distributivo);
    }

    // Saber la cantidad de docentes asignados a un distributivo
    public CantidadesDTO cantDocentesFaltAsig() {
        int cantDocentes = docenteRepo.findAll().size();
        int cantActDocentes = distributivoRepo.findDistinctDocentesByCicloId(cicloRepo.findByActivoTrue().getId()).size();

        double porcentajeAsig = (double) cantActDocentes / cantDocentes * 100;

        return CantidadesDTO.builder()
                .total(cantDocentes)
                .completo(cantActDocentes)
                .incompleto(cantDocentes - cantActDocentes)
                .porcentajeCompleto(porcentajeAsig)
                .porcentajeIncompleto(100 - porcentajeAsig)
                .build();
    }

    // Traer por curso
    public List<DistributivoDTO> getDistributivoByCurso(Long id) {
        return distributivoRepo.findByCursoId(id).stream()
                .map(this::convertirDisADTO)
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
                .map(this::convertirDisADTO)
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
                        d.getDocente().getId() == docenteRepo.findById(request.getIdDocente()).orElseThrow(() -> new ApiException(ApiResponse.builder()
                                .error(true)
                                .mensaje("Solicitud incorrecta")
                                .codigo(400)
                                .detalles("Docente no encontrado")
                                .build()
                        )).getId()
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
        distributivo.setDocente(docenteRepo.findById(request.getIdDocente()).get());

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
                .map(this::convertirDisADTO)
                .toList();
    }

    // traer por ciclo y docente
    public List<DistributivoDTO> getDistributivoByCicloAndDocente(Long cicloId, String cedula) {
        return distributivoRepo.findByCicloIdAndDocente_Usuario_Cedula(cicloId, cedula).stream()
                .map(this::convertirDisADTO)
                .toList();
    }

    // traer por ciclo id
    public List<DistributivoDTO> getDistributivoByCiclo(Long cicloId) {
    return distributivoRepo.findByCicloId(cicloId).stream()
            .sorted(Comparator.comparing((Distributivo d) -> d.getCurso().getGrado().getId())
                    .thenComparing(d -> Normalizer.normalize(d.getDocente().getUsuario().getApellidos(), Normalizer.Form.NFD)
                            .replaceAll("\\p{M}", "")))
            .map(this::convertirDisADTO)
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
    }

    private boolean validarExistenciaAulaMateria(DistributivoRequest request) {
        return distributivoRepo.existsByCursoIdAndMateriaIdAndCicloId(request.getAulaId(), request.getMateriaId(), request.getCicloId());
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

    private DistributivoDTO convertirDisADTO(Distributivo distributivo) {
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

    /* ------------------------------------- HORARIO ---------------------------------*/
    // Crear
    @Transactional
    public ApiResponse<String> crearHorario(HorarioRequest request) {
        // Traer el distributivo
        Distributivo distributivo = distributivoRepo.findById(request.getIdDistributivo())
                .orElseThrow(() -> new ApiException(ApiResponse.builder()
                        .error(true)
                        .mensaje("Solicitud inválida")
                        .codigo(404)
                        .detalles("El distributivo no ha sido encontrado")
                        .build()
                ));
        // Traer la config del horario
        HorarioConfig horarioConfig = horarioConfigRepo.findById(request.getIdHoraConfig())
                .orElseThrow(() -> new ApiException(ApiResponse.builder()
                        .error(true)
                        .mensaje("Solicitud inválida")
                        .codigo(404)
                        .detalles("La configuración del horario no ha sido encontrada")
                        .build()
                ));

        if (distributivo.getCurso() == null) {
            throw new ApiException(ApiResponse.builder()
                    .error(true)
                    .mensaje("Solicitud inválida")
                    .codigo(400)
                    .detalles("El distributivo no tiene un curso asignado")
                    .build()
            );
        }

        if(validarDistributivoHoras(request, 0) || validarHorario(request)) {
            throw new ApiException(ApiResponse.builder()
                    .error(true)
                    .mensaje("Solicitud inválida")
                    .codigo(400)
                    .detalles("El docente ya tiene un horario asignado en ese día y hora")
                    .build()
            );
        }

        // Traer las horas de la materia
        int horasMateria = distributivo.getMateria().getHoras();

        if (distributivo.getHorasAsignadas() == horasMateria) {
            throw new ApiException(ApiResponse.builder()
                    .error(true)
                    .mensaje("Solicitud inválida")
                    .codigo(400)
                    .detalles("La cantidad de horas asignadas ya es igual a las horas de la materia")
                    .build()
            );
        }

        // Obtener la cantidad de horas
        int cantHoras = distributivo.getHorasAsignadas() + 1;

        if (cantHoras > horasMateria) {
            throw new ApiException(ApiResponse.builder()
                    .error(true)
                    .mensaje("Solicitud inválida")
                    .codigo(400)
                    .detalles("La cantidad de horas asignadas supera las horas de la materia")
                    .build()
            );
        }

        // Actualizar las horas asignadas
        distributivo.setHorasAsignadas(cantHoras);
        distributivoRepo.save(distributivo);

        // Crear y guardar el horario
        Horario horario = Horario.builder()
                .cantHoras(cantHoras)
                .diaSemana(request.getDiaSemana())
                .horario(horarioConfig)
                .distributivo(distributivo)
                .build();

        horarioRepo.save(horario);
        return ApiResponse.<String>builder()
                .error(false)
                .mensaje("Horario creado")
                .codigo(200)
                .detalles("El horario ha sido creado correctamente")
                .build();
    }

    // Actualizar
    @Transactional
    public ApiResponse<String> editarHorario(HorarioRequest request) {
        // Traer el horario
        Horario horario = horarioRepo.findById(request.getId())
                .orElseThrow(() -> new ApiException(ApiResponse.builder()
                        .error(true)
                        .mensaje("Solicitud inválida")
                        .codigo(404)
                        .detalles("El horario no ha sido encontrado")
                        .build()
                ));

        // Traer el distributivo
        Distributivo distributivo = distributivoRepo.findById(request.getIdDistributivo())
                .orElseThrow(() -> new ApiException(ApiResponse.builder()
                        .error(true)
                        .mensaje("Solicitud inválida")
                        .codigo(404)
                        .detalles("El distributivo no encontrado")
                        .build()
                ));

        if (distributivo.getCurso() == null) {
            throw new ApiException(ApiResponse.builder()
                    .error(true)
                    .mensaje("Solicitud inválida")
                    .codigo(400)
                    .detalles("El distributivo no tiene un curso asignado")
                    .build()
            );
        }

        if(validarDistributivoHoras(request, request.getId())) {
            throw new ApiException(ApiResponse.builder()
                    .error(true)
                    .mensaje("Solicitud inválida")
                    .codigo(400)
                    .detalles("El docente ya tiene un horario asignado en ese día y hora")
                    .build()
            );
        }

        // Obtener la cantidad de horas
        int cantHoras = 0;

        // Traer las horas de la materia
        int horasMateria = distributivo.getMateria().getHoras();

        // Validar la cantidad de horas asignadas
        if (cantHoras <= 0) {
            throw new ApiException(ApiResponse.builder()
                    .error(true)
                    .mensaje("Solicitud inválida")
                    .codigo(400)
                    .detalles("La cantidad de horas no puede ser menor o igual a 0")
                    .build()
            );
        }

        if (distributivo.getHorasAsignadas() - horario.getCantHoras() + cantHoras >= horasMateria) {
            throw new ApiException(ApiResponse.builder()
                    .error(true)
                    .mensaje("Solicitud inválida")
                    .codigo(400)
                    .detalles("La cantidad de horas asignadas ya es igual a las horas de la materia")
                    .build()
            );
        }

        // Actualizar las horas asignadas
        distributivo.setHorasAsignadas(distributivo.getHorasAsignadas() - horario.getCantHoras() + cantHoras);
        distributivoRepo.save(distributivo);

        // Actualizar el horario
        horario.setCantHoras(cantHoras);
        horario.setDiaSemana(request.getDiaSemana());
        horario.setDistributivo(distributivo);

        horarioRepo.save(horario);

        return ApiResponse.<String>builder()
                .error(false)
                .mensaje("Horario actualizado")
                .codigo(200)
                .detalles("El horario ha sido actualizado correctamente")
                .build();



    }

    // Eliminar
    @Transactional
    public ApiResponse<String> eliminarHorario(long id) {
        // Traer el horario
        Horario horario = horarioRepo.findById(id)
                .orElseThrow(() -> new ApiException(ApiResponse.builder()
                        .error(true)
                        .mensaje("Solicitud inválida")
                        .codigo(404)
                        .detalles("El horario no ha sido encontrado")
                        .build()
                ));

        // Traer el distributivo
        Distributivo distributivo = horario.getDistributivo();

        // Actualizar las horas asignadas
        distributivo.setHorasAsignadas(distributivo.getHorasAsignadas() - horario.getCantHoras());
        distributivoRepo.save(distributivo);

        // Eliminar el horario
        horarioRepo.delete(horario);

        return ApiResponse.<String>builder()
                .error(false)
                .mensaje("Horario eliminado")
                .codigo(200)
                .detalles("El horario ha sido eliminado correctamente")
                .build();
    }

    // Traer horarios por curso
    public List<HorarioDTO> getHorariosByCurso(long id) {
        List<Horario> horario = horarioRepo.findByDistributivoCursoId(id);
        List<HorarioDTO> dto = new ArrayList<>();
        List<HorarioConfig> config = horarioConfigRepo.findAll();

        for (HorarioConfig h : config) {
            DiaDTO lunes = null, martes = null, miercoles = null, jueves = null, viernes = null;

            HoraDTO hora = HoraDTO.builder()
                    .horaInicio(String.valueOf(h.getHoraInicio()))
                    .horaFin(String.valueOf(h.getHoraFin()))
                    .build();

            for (Horario hor : horario) {

                if (h.getId() == hor.getHorario().getId()) {

                    switch (hor.getDiaSemana()) {
                        case Lunes:
                            lunes = DiaDTO.builder()
                                    .materia(hor.getDistributivo().getMateria().getNombre())
                                    .docente(hor.getDistributivo().getDocente().getUsuario().getApellidos() + " " + hor.getDistributivo().getDocente().getUsuario().getNombres())
                                    .build();
                            break;
                        case Martes:
                            martes = DiaDTO.builder()
                                    .materia(hor.getDistributivo().getMateria().getNombre())
                                    .docente(hor.getDistributivo().getDocente().getUsuario().getApellidos() + " " + hor.getDistributivo().getDocente().getUsuario().getNombres())
                                    .build();
                            break;
                        case Miercoles:
                            miercoles = DiaDTO.builder()
                                    .materia(hor.getDistributivo().getMateria().getNombre())
                                    .docente(hor.getDistributivo().getDocente().getUsuario().getApellidos() + " " + hor.getDistributivo().getDocente().getUsuario().getNombres())
                                    .build();
                            break;
                        case Jueves:
                            jueves = DiaDTO.builder()
                                    .materia(hor.getDistributivo().getMateria().getNombre())
                                    .docente(hor.getDistributivo().getDocente().getUsuario().getApellidos() + " " + hor.getDistributivo().getDocente().getUsuario().getNombres())
                                    .build();
                            break;
                        case Viernes:
                            viernes = DiaDTO.builder()
                                    .materia(hor.getDistributivo().getMateria().getNombre())
                                    .docente(hor.getDistributivo().getDocente().getUsuario().getApellidos() + " " + hor.getDistributivo().getDocente().getUsuario().getNombres())
                                    .build();
                            break;
                    }

                }
            }

            dto.add(HorarioDTO.builder()
                    .id(h.getId())
                    .horario(hora)
                    .lunes(lunes)
                    .martes(martes)
                    .miercoles(miercoles)
                    .jueves(jueves)
                    .viernes(viernes)
                    .build());
        }

        return dto;
    }

    // Traer horarios por docente
    public List<HorarioDTO> getHorariosByDocente(String cedDocen){
        List<Horario> horario = horarioRepo.findByDistributivoDocenteUsuarioCedula(cedDocen);
        List<HorarioDTO> dto = new ArrayList<>();
        List<HorarioConfig> config = horarioConfigRepo.findAll();

        for (HorarioConfig h : config) {
            DiaDTO lunes = null, martes = null, miercoles = null, jueves = null, viernes = null;

            HoraDTO hora = HoraDTO.builder()
                    .horaInicio(String.valueOf(h.getHoraInicio()))
                    .horaFin(String.valueOf(h.getHoraFin()))
                    .build();

            for (Horario hor : horario) {

                if (h.getId() == hor.getHorario().getId()) {
                    switch (hor.getDiaSemana()) {
                        case Lunes:
                            lunes = DiaDTO.builder()
                                    .materia(hor.getDistributivo().getMateria().getNombre())
                                    .curso(hor.getDistributivo().getCurso().getGrado().getNombre() + " " +  hor.getDistributivo().getCurso().getParalelo())
                                    .build();
                            break;
                        case Martes:
                            martes = DiaDTO.builder()
                                    .materia(hor.getDistributivo().getMateria().getNombre())
                                    .curso(hor.getDistributivo().getCurso().getGrado().getNombre() + " " +  hor.getDistributivo().getCurso().getParalelo())
                                    .build();
                            break;
                        case Miercoles:
                            miercoles = DiaDTO.builder()
                                    .materia(hor.getDistributivo().getMateria().getNombre())
                                    .curso(hor.getDistributivo().getCurso().getGrado().getNombre() + " " +  hor.getDistributivo().getCurso().getParalelo())
                                    .build();
                            break;
                        case Jueves:
                            jueves = DiaDTO.builder()
                                    .materia(hor.getDistributivo().getMateria().getNombre())
                                    .curso(hor.getDistributivo().getCurso().getGrado().getNombre() + " " +  hor.getDistributivo().getCurso().getParalelo())
                                    .build();
                            break;
                        case Viernes:
                            viernes = DiaDTO.builder()
                                    .materia(hor.getDistributivo().getMateria().getNombre())
                                    .curso(hor.getDistributivo().getCurso().getGrado().getNombre() + " " +  hor.getDistributivo().getCurso().getParalelo())
                                    .build();
                            break;
                    }
                }
            }
            dto.add(HorarioDTO.builder()
                    .id(h.getId())
                    .horario(hora)
                    .lunes(lunes)
                    .martes(martes)
                    .miercoles(miercoles)
                    .jueves(jueves)
                    .viernes(viernes)
                    .build());
        }
        return dto;
    }

    private boolean validarDistributivoHoras(HorarioRequest request, long idHorario) {
        // traer distributivos
        List<Horario> horarios = horarioRepo.findAll();
        // traer distributivo del request
        Distributivo distributivo = distributivoRepo.findById(request.getIdDistributivo()).orElseThrow(() -> new ApiException(ApiResponse.builder()
                .error(true)
                .mensaje("Solicitud inválida")
                .codigo(404)
                .detalles("El distributivo no ha sido encontrado")
                .build()
        ));

        for (Horario horario : horarios) {
            if(horario.getDistributivo().getDocente() == distributivo.getDocente()
                    && horario.getDiaSemana().equals(request.getDiaSemana())
                    && horario.getDistributivo().getMateria() == distributivo.getMateria()
                    && horario.getDistributivo().getCiclo() == distributivo.getCiclo()
                    && horario.getHorario().getId() == request.getIdHoraConfig()
                    && horario.getId() != idHorario) {

                return true;

            }
        }

        return false;
    }

    private boolean validarHorario(HorarioRequest request){
        // traer distributivos
        List<Horario> horarios = horarioRepo.findAll();
        // traer distributivo del request
        Distributivo distributivo = distributivoRepo.findById(request.getIdDistributivo()).orElseThrow(() -> new ApiException(ApiResponse.builder()
                .error(true)
                .mensaje("Solicitud inválida")
                .codigo(404)
                .detalles("El distributivo no ha sido encontrado")
                .build()
        ));

        for (Horario horario : horarios) {
            if(horario.getDiaSemana().equals(request.getDiaSemana())
                    && horario.getDistributivo().getCiclo() == distributivo.getCiclo()
                    && horario.getDistributivo().getCurso() == distributivo.getCurso()
                    && horario.getHorario().getId() == request.getIdHoraConfig()
                    && horario.getId() != 0) {

                return true;

            }
        }

        return false;
    }

}
