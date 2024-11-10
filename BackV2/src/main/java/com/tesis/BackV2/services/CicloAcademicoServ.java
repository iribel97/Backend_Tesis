package com.tesis.BackV2.services;

import com.tesis.BackV2.entities.CicloAcademico;
import com.tesis.BackV2.repositories.CicloAcademicoRepo;
import com.tesis.BackV2.request.CicloARequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CicloAcademicoServ {

    @Autowired
    private CicloAcademicoRepo repo;

    // Creación de un ciclo académico
    public String crearCicloAcademico(CicloARequest request) {
        // Verificar que el usuario sea de tipo administrador




        List<CicloAcademico> ciclos = repo.findAll();
        if (ciclos.size() > 0) {
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


}
