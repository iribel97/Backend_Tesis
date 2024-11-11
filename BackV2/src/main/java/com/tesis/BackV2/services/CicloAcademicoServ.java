package com.tesis.BackV2.services;

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

        var distributivo = Distributivo.builder()
                .ciclo(cicloRepo.findById(request.getCicloId()).get())
                .aula(aulaRepo.findById(request.getAulaId()).get())
                .materia(materiaRepo.findById(request.getMateriaId()).get())
                .docente(docenteRepo.findByUsuarioCedula(request.getCedulaDocente()))
                .build();

        distributivoRepo.save(distributivo);
        return "Distributivo creado";
    }

}
