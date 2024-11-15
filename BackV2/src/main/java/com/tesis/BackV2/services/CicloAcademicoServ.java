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
    // Creación de un ciclo académico
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

    // Traer todos los ciclos académicos
    public List<CicloAcademico> getCiclos() {
        return cicloRepo.findAll();
    }

    // Traer un solo ciclo academico
    public CicloAcademico getCiclo(Long id) {
        return cicloRepo.findById(id).orElseThrow(() -> new RuntimeException("Ciclo académico no encontrado"));
    }

    /* -------------------- GRADOS ACADEMICOS -------------------- */
    // Creación de grados académicos ejem: octavo, noveno, decimo
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

    // Traer todos los grados académicos
    public List<Grado> getGrados() {
        return gradoRepo.findAll();
    }

    // Traer un solo grado academico
    public Grado getGrado(String nombre) {
        Grado grado = gradoRepo.findByNombre(nombre);
        if(grado == null) {
            throw new RuntimeException("Grado académico no encontrado");
        }
        return gradoRepo.findByNombre(nombre);
    }

    /* -------------------- CURSOS/AULAS ACADEMICAS -------------------- */
    // Creación de aulas
    public String crearAula(AulaRequest request) {
        // Verificar si el paralelo  y el grado ya existe
        if (aulaRepo.existsByParalelo(request.getParalelo()) && aulaRepo.existsByGradoId(gradoRepo.findByNombre(request.getGrado()).getId())) {
            throw new RuntimeException("El paralelo ya existe");
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

    // Traer todas las aulas
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

    // Traer solo un aula
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

    /* -------------------- MATERIAS ACADEMICAS -------------------- */
    // Crear materia
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

    // Traer todas las materias
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

    // Traer solo una materia
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
    /* -------------------- DISTRIBUTIVO -------------------- */
    // Crear distributivo
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

    // Traer todos los distributivos
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

    // Traer distributivo por id
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

    // Traer distributivo por ciclo academico
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

}
