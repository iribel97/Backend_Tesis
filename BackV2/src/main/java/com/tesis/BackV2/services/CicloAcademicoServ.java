package com.tesis.BackV2.services;

import com.tesis.BackV2.entities.Aula;
import com.tesis.BackV2.entities.CicloAcademico;
import com.tesis.BackV2.entities.Grado;
import com.tesis.BackV2.enums.Rol;
import com.tesis.BackV2.repositories.*;
import com.tesis.BackV2.request.AulaRequest;
import com.tesis.BackV2.request.CicloARequest;
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

}
