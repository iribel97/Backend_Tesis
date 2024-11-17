package com.tesis.BackV2.services;

import com.tesis.BackV2.dto.AulaDTO;
import com.tesis.BackV2.dto.DistributivoDTO;
import com.tesis.BackV2.dto.MateriaDTO;
import com.tesis.BackV2.entities.*;
import com.tesis.BackV2.enums.Rol;
import com.tesis.BackV2.repositories.*;
import com.tesis.BackV2.request.AulaRequest;
import com.tesis.BackV2.request.CicloARequest;
import com.tesis.BackV2.request.DistributivoRequest;
import com.tesis.BackV2.request.MateriaRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
public class CicloAcademicoServ {

    @Autowired
    private CicloAcademicoRepo cicloRepo;
    @Autowired
    private GradoRepo gradoRepo;
    @Autowired
    private AulaRepo aulaRepo;
    @Autowired
    private DocenteRepo docenteRepo;
    @Autowired
    private UsuarioRepo usuarioRepo;
    @Autowired
    private MateriaRepo materiaRepo;
    @Autowired
    private DistributivoRepo distributivoRepo;

    /* -------------------- CICLO ACADEMICO -------------------- */
    // Creación
    @Transactional
    public String crearCicloAcademico(CicloARequest request) {
        if (cicloRepo.findAll().stream().anyMatch(
                ciclo -> request.getFechaInicio().isBefore(ciclo.getFechaFin()) &&
                        request.getFechaFin().isAfter(ciclo.getFechaInicio()))) {
            throw new RuntimeException("El ciclo académico choca con otro ciclo académico");
        }

        CicloAcademico ciclo = CicloAcademico.builder()
                .nombre(request.getNombre())
                .cantPeriodos(request.getCantPeriodos())
                .fechaInicio(request.getFechaInicio())
                .fechaFin(request.getFechaFin())
                .build();

        cicloRepo.save(ciclo);
        return "Ciclo académico creado";
    }

    // Traer todos
    public List<CicloAcademico> getCiclos() {
        return cicloRepo.findAll();
    }

    // Traer un solo por id
    public CicloAcademico getCiclo(Long id) {
        return cicloRepo.findById(id).orElseThrow(() -> new RuntimeException("Ciclo académico no encontrado"));
    }

    // Editar
    @Transactional
    public String editarCiclo(CicloARequest request){

        // Traer el ciclo academico a editar
        CicloAcademico ciclo = cicloRepo.findById(request.getId()).orElseThrow(() -> new RuntimeException("Ciclo académico no encontrado"));

        ciclo.setNombre(request.getNombre());
        ciclo.setCantPeriodos(request.getCantPeriodos());
        ciclo.setFechaInicio(request.getFechaInicio());
        ciclo.setFechaFin(request.getFechaFin());

        cicloRepo.save(ciclo);

        return("Actualización completa");

    }

    // Eliminar
    @Transactional
    public String eliminarCiclo(Long id) {
        CicloAcademico ciclo = cicloRepo.findById(id).orElseThrow(() -> new RuntimeException("Ciclo académico no encontrado"));

        // comprobar si hay distributivos asociados
        if (distributivoRepo.existsByCicloId(id)) {
            throw new RuntimeException("No se puede eliminar el ciclo académico porque tiene distributivos asociados");
        }

        cicloRepo.delete(ciclo);
        return "Ciclo académico eliminado";
    }

    /* -------------------- GRADOS ACADEMICOS -------------------- */
    // Creación ejem: octavo, noveno, decimo
    @Transactional
    public String crearGrado(Grado request) {
        boolean gradoExiste = gradoRepo.findAll().stream()
                .anyMatch(grado -> grado.getNombre().equalsIgnoreCase(request.getNombre()));
        if (gradoExiste) {
            throw new RuntimeException("Ya existen los grados académicos");
        }

        Grado grado = Grado.builder()
                .nombre(request.getNombre())
                .build();

        gradoRepo.save(grado);
        return "Grado creado correctamente";
    }

    // Traer todos
    public List<Grado> getGrados() { return gradoRepo.findAll(); }

    // Traer por nombre
    public Grado getGrado(String nombre) {
        Grado grado = gradoRepo.findByNombre(nombre);
        if(grado == null) {
            throw new RuntimeException("Grado académico no encontrado");
        }
        return gradoRepo.findByNombre(nombre);
    }

    // Acualizar
    @Transactional
    public String editarGrado(Grado request){
        // Traer el grado academico a editar
        Grado grado = gradoRepo.findById(request.getId()).orElseThrow(() -> new RuntimeException("Grado académico no encontrado"));

        // Compobar que el nombre no sea el mismo que los otros
        List<Grado> list = gradoRepo.findAll();

        for(Grado grado1 : list){
            if(grado1.getId() != request.getId() && grado1.getNombre().equalsIgnoreCase(request.getNombre())){
                throw new RuntimeException("El nombre del grado ya existe");
            }

        }

        grado.setNombre(request.getNombre());

        gradoRepo.save(grado);

        return("Actualización completa");
    }

    // Eliminar
    @Transactional
    public String eliminarGrado(Long id){
        Grado grado = gradoRepo.findById(id).orElseThrow(() -> new RuntimeException("Grado académico no encontrado"));

        // comprobar si hay aulas o materias asociadas
        if (aulaRepo.existsByGradoId(id) || materiaRepo.existsByGradoId(id)) {
            throw new RuntimeException("No se puede eliminar el grado académico porque tiene aulas o materias asociadas");
        }

        gradoRepo.delete(grado);

        return("Grado académico eliminado");
    }

    /* -------------------- CURSOS/AULAS ACADEMICAS -------------------- */
    // Creación
    @Transactional
    public String crearAula(AulaRequest request) {
        // Verificar si el paralelo  y el grado ya existe
        List<Aula> aulas = aulaRepo.findAll();

        for (Aula aula : aulas) {
            if (aula.getParalelo().equalsIgnoreCase(request.getParalelo()) && aula.getGrado().getNombre().equalsIgnoreCase(request.getGrado())) {
                throw new RuntimeException("El paralelo ya existe");
            }

        }

        // Verificar si el tutor ya tiene un aula asignada
        if (aulaRepo.existsByTutorId(docenteRepo.findByUsuarioCedula(request.getCedulaTutor()).getId())) {
            throw new RuntimeException("El tutor ya tiene un aula asignada");

        }

        var usuario = usuarioRepo.findByCedula(request.getCedulaTutor());
        if (usuarioRepo.existsByCedula(request.getCedulaTutor()) && !usuario.getRol().equals(Rol.DOCENTE)) {
            throw new RuntimeException("El tutor no existe o no es un docente");
        }

        Aula aula = Aula.builder()
                .paralelo(request.getParalelo())
                .maxEstudiantes(request.getCantEstudiantes())
                .grado(gradoRepo.findByNombre(request.getGrado()))
                .tutor(docenteRepo.findByUsuarioCedula(request.getCedulaTutor()))
                .build();

        aulaRepo.save(aula);
        return "Creación de aula exitosa";
    }

    // Traer todas
    public List<AulaDTO> getAulas() {
        List<Aula> aulas = aulaRepo.findAll();
        return aulas.stream().map(aula -> AulaDTO.builder()
                .id(aula.getId())
                .paralelo(aula.getParalelo())
                .maxEstudiantes(aula.getMaxEstudiantes())
                .nombreGrado(aula.getGrado().getNombre())
                .tutor(aula.getTutor().getUsuario().getNombres() + " " + aula.getTutor().getUsuario().getApellidos())
                .telefonoTutor(aula.getTutor().getUsuario().getTelefono())
                .emailTutor(aula.getTutor().getUsuario().getEmail())
                .build()).toList();
    }

    // Traer solo uno
    public AulaDTO getAula(String paralelo, String grado) {
        Aula aula = aulaRepo.findByParaleloAndGradoNombre(paralelo, grado);

        if(aula == null){
            throw new RuntimeException("Aula no encontrada");
        }

        return AulaDTO.builder()
                .id(aula.getId())
                .paralelo(aula.getParalelo())
                .maxEstudiantes(aula.getMaxEstudiantes())
                .nombreGrado(aula.getGrado().getNombre())
                .tutor(aula.getTutor().getUsuario().getNombres() + " " + aula.getTutor().getUsuario().getApellidos())
                .telefonoTutor(aula.getTutor().getUsuario().getTelefono())
                .emailTutor(aula.getTutor().getUsuario().getEmail())
                .build();
    }

    // Actualizar
    @Transactional
    public String editarAula(AulaRequest request) {
        Aula aulaE = aulaRepo.findById(request.getId()).orElseThrow(() -> new RuntimeException("Aula no encontrada"));

        List<Aula> aulas = aulaRepo.findAll();

        Docente docente = docenteRepo.findByUsuarioCedula(request.getCedulaTutor());

        for (Aula aula : aulas) {
            if (aula.getId() != request.getId()
                && aula.getParalelo().equalsIgnoreCase(request.getParalelo())
                && aula.getGrado().getNombre().equalsIgnoreCase(request.getGrado())) {

                throw new RuntimeException("El paralelo ya existe");
            }
        }

        if (aulaRepo.existsByTutorId(docente.getId()) && aulaE.getTutor().getId() != docente.getId()) {
            throw new RuntimeException("El tutor ya tiene un aula asignada");
        }

        aulaE.setParalelo(request.getParalelo());
        aulaE.setMaxEstudiantes(request.getCantEstudiantes());
        aulaE.setGrado(gradoRepo.findByNombre(request.getGrado()));
        aulaE.setTutor(docente);

        aulaRepo.save(aulaE);

        return "Actualización completa";
    }

    // Eliminar
    @Transactional
    public String eliminarAula(Long id){
        Aula aula = aulaRepo.findById(id).orElseThrow(() -> new RuntimeException("Aula no encontrada"));

        // comprobar si hay distributivos asociados
        if (distributivoRepo.existsByAulaId(id)) {
            throw new RuntimeException("No se puede eliminar el aula porque tiene distributivos asociados");
        }

        aulaRepo.delete(aula);

        return("Aula eliminada");
    }

    /* -------------------- MATERIAS ACADEMICAS -------------------- */
    // Crear
    @Transactional
    public String crearMateria(MateriaRequest request) {
        // Verificar si el grado existe
        if (Objects.isNull(gradoRepo.findByNombre(request.getGrado()))) {
            throw new RuntimeException("El grado no existe");
        }

        // Verificar si la materia ya existe
        if (materiaRepo.existsByNombre(request.getNombre()) && materiaRepo.existsByGradoNombre(request.getGrado())) {
            throw new RuntimeException("La materia ya existe en el grado");
        }

        Materia materia = Materia.builder()
                .nombre(request.getNombre())
                .horas(request.getHoras())
                .area(request.getArea())
                .grado(gradoRepo.findByNombre(request.getGrado()))
                .build();

        materiaRepo.save(materia);
        return "Creación de materia exitosa";
    }

    // Traer todas
    public List<MateriaDTO> getMaterias() {
        List<Materia> materias = materiaRepo.findAll();
        return materias.stream().map(materia -> MateriaDTO.builder()
                .id(materia.getId())
                .nombre(materia.getNombre())
                .area(materia.getArea())
                .horasSemanales(materia.getHoras())
                .nombreGrado(materia.getGrado().getNombre())
                .build()).toList();
    }

    // Traer solo una
    public MateriaDTO getMateria(long id){
        Materia materia = materiaRepo.findById(id).orElseThrow(() -> new RuntimeException("Materia no encontrada"));
        return MateriaDTO.builder()
                .id(materia.getId())
                .nombre(materia.getNombre())
                .area(materia.getArea())
                .horasSemanales(materia.getHoras())
                .nombreGrado(materia.getGrado().getNombre())
                .build();
    }

    // Actualizar
    @Transactional
    public String editarMateria(MateriaRequest request) {
        Materia materia = materiaRepo.findById(request.getId()).orElseThrow(() -> new RuntimeException("Materia no encontrada"));

        List<Materia> materias = materiaRepo.findAll();

        // Verificar si el grado existe
        if (Objects.isNull(gradoRepo.findByNombre(request.getGrado()))) {
            throw new RuntimeException("El grado no existe");
        }

        for (Materia materia1 : materias) {
            if (materia1.getId() != request.getId() && materia1.getNombre().equalsIgnoreCase(request.getNombre()) && materia1.getGrado().getNombre().equalsIgnoreCase(request.getGrado())) {
                throw new RuntimeException("La materia ya existe en el grado");
            }
        }

        materia.setNombre(request.getNombre());
        materia.setHoras(request.getHoras());
        materia.setArea(request.getArea());
        materia.setGrado(gradoRepo.findByNombre(request.getGrado()));

        materiaRepo.save(materia);

        return "Actualización completa";
    }

    // Eliminar
    @Transactional
    public String eliminarMateria(Long id){
        Materia materia = materiaRepo.findById(id).orElseThrow(() -> new RuntimeException("Materia no encontrada"));

        // comprobar si hay distributivos asociados
        if (distributivoRepo.existsByMateriaId(id)) {
            throw new RuntimeException("No se puede eliminar la materia porque tiene distributivos asociados");
        }

        materiaRepo.delete(materia);

        return("Materia eliminada");
    }

    /* -------------------- DISTRIBUTIVO -------------------- */
    // Crear
    public String crearDistributivo(DistributivoRequest request) {
        // Verificar que los datos no se repitan
        List<Distributivo> distributicvos = distributivoRepo.findAll();
        if (distributicvos.stream().anyMatch(distributivo -> distributivo.getCiclo().getId() == request.getCicloId() &&
                distributivo.getAula().getId() == request.getAulaId() &&
                distributivo.getMateria().getId() == request.getMateriaId() &&
                distributivo.getDocente().getId() == docenteRepo.findByUsuarioCedula(request.getCedulaDocente()).getId())) {
            throw new RuntimeException("El distributivo ya existe");
        }
        // Verificar si el ciclo académico existe
        if (!cicloRepo.existsById(request.getCicloId())) {
            throw new RuntimeException("El ciclo académico no existe");
        }

        // Verificar si el aula existe
        if (!aulaRepo.existsById(request.getAulaId())) {
            throw new RuntimeException("El aula no existe");
        }

        // Verificar si la materia existe
        if (!materiaRepo.existsById(request.getMateriaId())) {
            throw new RuntimeException("La materia no existe");
        }

        // Verificar si el docente existe
        if (!usuarioRepo.existsByCedula(request.getCedulaDocente()) && !usuarioRepo.findByCedula(request.getCedulaDocente()).getRol().equals(Rol.DOCENTE)) {
            throw new RuntimeException("El docente no existe");
        }

        // Verificar si el grado de la materia es el mismo que el grado del aula
        if (materiaRepo.findById(request.getMateriaId()).get().getGrado().getId() != aulaRepo.findById(request.getAulaId()).get().getGrado().getId()) {
            throw new RuntimeException("El grado de la materia no coincide con el grado del curso");
        }

        var distributivo = Distributivo.builder()
                .ciclo(cicloRepo.findById(request.getCicloId()).get())
                .aula(aulaRepo.findById(request.getAulaId()).get())
                .materia(materiaRepo.findById(request.getMateriaId()).get())
                .docente(docenteRepo.findByUsuarioCedula(request.getCedulaDocente()))
                .build();

        distributivoRepo.save(distributivo);
        return "Distributivo creado";
    }

    // Traer todos
    public List<DistributivoDTO> getDistributivos() {
        List<Distributivo> distributivos = distributivoRepo.findAll();
        return distributivos.stream().map(distributivo -> DistributivoDTO.builder()
                .id(distributivo.getId())
                .cicloAcademico(distributivo.getCiclo().getNombre())
                .aula(distributivo.getAula().getParalelo())
                .grado(distributivo.getAula().getGrado().getNombre())
                .materia(distributivo.getMateria().getNombre())
                .horasSemanales(distributivo.getMateria().getHoras())
                .docente(distributivo.getDocente().getUsuario().getNombres() + " " + distributivo.getDocente().getUsuario().getApellidos())
                .build()).toList();
    }

    // Traer por id
    public DistributivoDTO getDistributivo(Long id) {
        Distributivo distributivo = distributivoRepo.findById(id).orElseThrow(() -> new RuntimeException("Distributivo no encontrado"));
        return DistributivoDTO.builder()
                .id(distributivo.getId())
                .cicloAcademico(distributivo.getCiclo().getNombre())
                .aula(distributivo.getAula().getParalelo())
                .grado(distributivo.getAula().getGrado().getNombre())
                .materia(distributivo.getMateria().getNombre())
                .horasSemanales(distributivo.getMateria().getHoras())
                .docente(distributivo.getDocente().getUsuario().getNombres() + " " + distributivo.getDocente().getUsuario().getApellidos())
                .build();
    }

    // Traer por ciclo academico
    public List<DistributivoDTO> getDistributivoByCiclo(Long id) {
        List<Distributivo> distributivos = distributivoRepo.findByCicloId(id);
        return distributivos.stream().map(distributivo -> DistributivoDTO.builder()
                .id(distributivo.getId())
                .cicloAcademico(distributivo.getCiclo().getNombre())
                .aula(distributivo.getAula().getParalelo())
                .grado(distributivo.getAula().getGrado().getNombre())
                .materia(distributivo.getMateria().getNombre())
                .horasSemanales(distributivo.getMateria().getHoras())
                .docente(distributivo.getDocente().getUsuario().getNombres() + " " + distributivo.getDocente().getUsuario().getApellidos())
                .build()).toList();
    }

    // Actualizar
    @Transactional
    public String editarDistributivo(DistributivoRequest request) {
        Distributivo distributivo = distributivoRepo.findById(request.getId()).orElseThrow(() -> new RuntimeException("Distributivo no encontrado"));

        // Verificar que los datos no se repitan
        List<Distributivo> distributivos = distributivoRepo.findAll();
        if (distributivos.stream().anyMatch(distributivo1 -> distributivo1.getId() != request.getId() &&
                distributivo1.getCiclo().getId() == request.getCicloId() &&
                distributivo1.getAula().getId() == request.getAulaId() &&
                distributivo1.getMateria().getId() == request.getMateriaId() &&
                distributivo1.getDocente().getId() == docenteRepo.findByUsuarioCedula(request.getCedulaDocente()).getId())) {
            throw new RuntimeException("El distributivo ya existe");
        }

        if (!cicloRepo.existsById(request.getCicloId()) ||
                !aulaRepo.existsById(request.getAulaId()) ||
                !materiaRepo.existsById(request.getMateriaId()) ||
                !usuarioRepo.existsByCedula(request.getCedulaDocente())
        ) {
            throw new RuntimeException("Parametro no encontrado");
        }

        // Verificar si el grado de la materia es el mismo que el grado del aula
        if (materiaRepo.findById(request.getMateriaId()).get().getGrado().getId() != aulaRepo.findById(request.getAulaId()).get().getGrado().getId()) {
            throw new RuntimeException("El grado de la materia no coincide con el grado del curso");
        }

        distributivo.setCiclo(cicloRepo.findById(request.getCicloId()).get());
        distributivo.setAula(aulaRepo.findById(request.getAulaId()).get());
        distributivo.setMateria(materiaRepo.findById(request.getMateriaId()).get());
        distributivo.setDocente(docenteRepo.findByUsuarioCedula(request.getCedulaDocente()));

        distributivoRepo.save(distributivo);
        return "Distributivo actualizado";

    }

    // Eliminar
    @Transactional
    public String eliminarDistributivo(Long id){
        Distributivo distributivo = distributivoRepo.findById(id).orElseThrow(() -> new RuntimeException("Distributivo no encontrado"));

        distributivoRepo.delete(distributivo);

        return("Distributivo eliminado");
    }

}
