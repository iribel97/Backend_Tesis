package com.tesis.BackV2.services;

import com.tesis.BackV2.entities.CicloAcademico;
import com.tesis.BackV2.entities.Grado;
import com.tesis.BackV2.repositories.CicloAcademicoRepo;
import com.tesis.BackV2.repositories.GradoRepo;
import com.tesis.BackV2.request.CicloARequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CicloAcademicoServ {

    @Autowired
    private CicloAcademicoRepo repo;
    @Autowired
    private GradoRepo gradoRepo;

    // Creación de un ciclo académico
    public String crearCicloAcademico(CicloARequest request) {
        List<CicloAcademico> ciclos = repo.findAll();
        if (!ciclos.isEmpty()) {
            //Verificar que el ciclo academico no choque con otro ciclo academico
            for (CicloAcademico ciclo : ciclos) {
                if (request.getFechaInicio().isBefore(ciclo.getFechaFin()) && request.getFechaFin().isAfter(ciclo.getFechaInicio())) {
                    throw new RuntimeException("El ciclo académico choca con otro ciclo académico");
                }
            }
        }

        // Crear un nuevo ciclo académico
        var ciclo = CicloAcademico.builder()
                .nombre(request.getNombre())
                .cantPeriodos(request.getCantPeriodos())
                .fechaInicio(request.getFechaInicio())
                .fechaFin(request.getFechaFin())
                .build();

        // Guardar el ciclo académico en la base de datos
        repo.save(ciclo);

        return "Ciclo académico creado";
    }

    // Creación de grados académicos octavo, noveno, decimo
    public String crearGrado(Grado request){
        List<Grado> grados = gradoRepo.findAll();
        if (!grados.isEmpty()){
            // Verificar que no exista el mismo nombre de grado
            for (Grado grado : grados){
                if (grado.getNombre().equalsIgnoreCase(request.getNombre()) ){
                    throw new RuntimeException("Ya existen los grados académicos");
                }
            }
        }

        // Crear un nuevo grado académico
        var grado = Grado.builder()
                .nombre(request.getNombre())
                .build();

        // Guardar el grado académico en la base de datos
        gradoRepo.save(grado);

        return "Grado creado correctamente";
    }


}
