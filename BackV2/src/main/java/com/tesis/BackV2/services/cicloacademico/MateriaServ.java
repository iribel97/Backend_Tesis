package com.tesis.BackV2.services.cicloacademico;

import com.tesis.BackV2.dto.MateriaDTO;
import com.tesis.BackV2.entities.Materia;
import com.tesis.BackV2.entities.SistemaCalificacion;
import com.tesis.BackV2.enums.TipoNivel;
import com.tesis.BackV2.repositories.*;
import com.tesis.BackV2.request.CalfRequest;
import com.tesis.BackV2.request.MateriaRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MateriaServ {

    @Autowired
    private GradoRepo gradoRepo;
    @Autowired
    private MateriaRepo materiaRepo;
    @Autowired
    private DistributivoRepo distributivoRepo;
    @Autowired
    private SistCalifRepo sistCalifRepo;

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
                .registroCalificacion(request.getRegistroCalificacion())
                .build();

        materiaRepo.save(materia);
        return "Creación de materia exitosa";
    }

    // Obtener todas
    public List<MateriaDTO> getMaterias() {
        List<Materia> materias = materiaRepo.findAll();
        List<MateriaDTO> materiasDTO = new ArrayList<>();
        materias.forEach(materia -> materiasDTO.add(convertirADTO(materia)));
        return materiasDTO;
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
        List<SistemaCalificacion> sistemaCalificacion = sistCalifRepo.porRegistro(materia.getRegistroCalificacion());

        return MateriaDTO.builder()
                .id(materia.getId())
                .nombre(materia.getNombre())
                .area(materia.getArea())
                .horasSemanales(materia.getHoras())
                .nombreGrado(materia.getGrado().getNombre())
                .sistemaCalificacion(sistemaCalificacion.stream()
                        .map(sistema -> CalfRequest.builder()
                                .nivel(niveles(sistema))
                                .tipo(sistema.getTipo())
                                .descripcion(sistema.getDescripcion())
                                .peso(sistema.getPeso())
                                .build())
                        .collect(Collectors.toList()))
                .build();
    }

    // pasar niveles de calificaciones

    private TipoNivel niveles (SistemaCalificacion sistema){
         if (sistema.getId().getLvl2() == 0 && sistema.getId().getLvl3() == 0 && sistema.getId().getLvl4() == 0){
             return TipoNivel.Primero;
         } else if (sistema.getId().getLvl3() == 0 && sistema.getId().getLvl4() == 0){
             return TipoNivel.Segundo;
         } else if (sistema.getId().getLvl4() == 0){
             return TipoNivel.Tercero;
         } else {
             return TipoNivel.Cuarto;
         }
    }


}
