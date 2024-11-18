package com.tesis.BackV2.services.cicloacademico;

import com.tesis.BackV2.entities.Grado;
import com.tesis.BackV2.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class GradoServ {
    @Autowired
    private GradoRepo gradoRepo;
    @Autowired
    private AulaRepo aulaRepo;
    @Autowired
    private MateriaRepo materiaRepo;


    // Creación ejem: octavo, noveno, decimo
    @Transactional
    public String crearGrado(Grado request) {
        boolean gradoExiste = gradoRepo.existsByNombreIgnoreCase(request.getNombre());
        if (gradoExiste) {
            throw new RuntimeException("Ya existe un grado académico con el mismo nombre.");
        }

        Grado grado = Grado.builder()
                .nombre(request.getNombre())
                .build();

        gradoRepo.save(grado);
        return "Grado académico creado correctamente.";
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
    public String editarGrado(Grado request) {
        Grado grado = gradoRepo.findById(request.getId()).orElseThrow(() -> new RuntimeException("Grado académico no encontrado."));

        boolean nombreEnUso = gradoRepo.existsByNombreIgnoreCaseAndIdNot(request.getNombre(), request.getId());
        if (nombreEnUso) {
            throw new RuntimeException("El nombre del grado académico ya está en uso.");
        }

        grado.setNombre(request.getNombre());
        gradoRepo.save(grado);

        return "Grado académico actualizado correctamente.";
    }

    // Eliminar
    @Transactional
    public String eliminarGrado(Long id) {
        Grado grado = gradoRepo.findById(id).orElseThrow(() -> new RuntimeException("Grado académico no encontrado."));

        if (aulaRepo.existsByGradoId(id) || materiaRepo.existsByGradoId(id)) {
            throw new RuntimeException("No se puede eliminar el grado académico porque tiene aulas o materias asociadas.");
        }

        gradoRepo.delete(grado);
        return "Grado académico eliminado correctamente.";
    }
}
