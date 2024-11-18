package com.tesis.BackV2.services.cicloacademico;

import com.tesis.BackV2.repositories.*;
import com.tesis.BackV2.entities.*;
import com.tesis.BackV2.request.CicloARequest;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CicloServ {
    @Autowired
    private CicloAcademicoRepo cicloRepo;
    @Autowired
    private DistributivoRepo distributivoRepo;

    // Creación
    @Transactional
    public String crearCicloAcademico(CicloARequest request) {
        boolean existeConflicto = cicloRepo.findAll().stream()
                .anyMatch(ciclo ->
                        request.getFechaInicio().isBefore(ciclo.getFechaFin()) &&
                                request.getFechaFin().isAfter(ciclo.getFechaInicio())
                );

        if (existeConflicto) {
            throw new RuntimeException("El ciclo académico choca con otro ciclo académico.");
        }

        CicloAcademico ciclo = CicloAcademico.builder()
                .nombre(request.getNombre())
                .cantPeriodos(request.getCantPeriodos())
                .fechaInicio(request.getFechaInicio())
                .fechaFin(request.getFechaFin())
                .build();

        cicloRepo.save(ciclo);
        return "Ciclo académico creado exitosamente.";
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
    public String editarCiclo(CicloARequest request) {
        CicloAcademico ciclo = cicloRepo.findById(request.getId())
                .orElseThrow(() -> new RuntimeException("Ciclo académico no encontrado."));

        ciclo.setNombre(request.getNombre());
        ciclo.setCantPeriodos(request.getCantPeriodos());
        ciclo.setFechaInicio(request.getFechaInicio());
        ciclo.setFechaFin(request.getFechaFin());

        cicloRepo.save(ciclo);
        return "Ciclo académico actualizado exitosamente.";
    }

    // Eliminar
    @Transactional
    public String eliminarCiclo(Long id) {
        CicloAcademico ciclo = cicloRepo.findById(id).orElseThrow(() -> new RuntimeException("Ciclo académico no encontrado."));

        if (distributivoRepo.existsByCicloId(id)) {
            throw new RuntimeException("No se puede eliminar el ciclo académico porque tiene distributivos asociados.");
        }

        cicloRepo.delete(ciclo);
        return "Ciclo académico eliminado exitosamente.";
    }
}
