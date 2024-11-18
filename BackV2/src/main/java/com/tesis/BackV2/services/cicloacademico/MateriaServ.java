package com.tesis.BackV2.services.cicloacademico;

import com.tesis.BackV2.dto.MateriaDTO;
import com.tesis.BackV2.entities.Materia;
import com.tesis.BackV2.repositories.*;
import com.tesis.BackV2.request.MateriaRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class MateriaServ {

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
    @Autowired
    private HorarioRepo horarioRepo;

    // Crear
    @Transactional
    public String crearMateria(MateriaRequest request) {
        var grado = gradoRepo.findByNombre(request.getGrado());
        if (grado == null) {
            throw new RuntimeException("El grado especificado no existe.");
        }

        boolean materiaExiste = materiaRepo.existsByNombreAndGradoNombre(request.getNombre(), request.getGrado());
        if (materiaExiste) {
            throw new RuntimeException("La materia ya existe en el grado especificado.");
        }

        Materia materia = Materia.builder()
                .nombre(request.getNombre())
                .horas(request.getHoras())
                .area(request.getArea())
                .grado(grado)
                .build();

        materiaRepo.save(materia);
        return "Creación de materia exitosa";
    }

    // Obtener todas
    public List<MateriaDTO> getMaterias() {
        return materiaRepo.findAll().stream()
                .map(this::convertirADTO)
                .toList();
    }

    // Obtener una
    public MateriaDTO getMateria(long id) {
        Materia materia = materiaRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Materia no encontrada."));
        return convertirADTO(materia);
    }

    // Actualizar
    @Transactional
    public String editarMateria(MateriaRequest request) {
        Materia materia = materiaRepo.findById(request.getId())
                .orElseThrow(() -> new RuntimeException("Materia no encontrada."));

        var grado = gradoRepo.findByNombre(request.getGrado());
        if (grado == null) {
            throw new RuntimeException("El grado especificado no existe.");
        }

        boolean materiaExiste = materiaRepo.existsByNombreAndGradoNombre(request.getNombre(), request.getGrado())
                && !materia.getNombre().equalsIgnoreCase(request.getNombre());
        if (materiaExiste) {
            throw new RuntimeException("La materia ya existe en el grado especificado.");
        }

        materia.setNombre(request.getNombre());
        materia.setHoras(request.getHoras());
        materia.setArea(request.getArea());
        materia.setGrado(grado);

        materiaRepo.save(materia);
        return "Actualización completa";
    }

    // Eliminar
    @Transactional
    public String eliminarMateria(Long id) {
        Materia materia = materiaRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Materia no encontrada."));

        if (distributivoRepo.existsByMateriaId(id)) {
            throw new RuntimeException("No se puede eliminar la materia porque tiene distributivos asociados.");
        }

        materiaRepo.delete(materia);
        return "Materia eliminada";
    }

    // Convertir a DTO
    private MateriaDTO convertirADTO(Materia materia) {
        return MateriaDTO.builder()
                .id(materia.getId())
                .nombre(materia.getNombre())
                .area(materia.getArea())
                .horasSemanales(materia.getHoras())
                .nombreGrado(materia.getGrado().getNombre())
                .build();
    }


}
