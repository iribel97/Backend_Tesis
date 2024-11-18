package com.tesis.BackV2.services.cicloacademico;

import com.tesis.BackV2.dto.DistributivoDTO;
import com.tesis.BackV2.entities.Distributivo;
import com.tesis.BackV2.enums.Rol;
import com.tesis.BackV2.repositories.*;
import com.tesis.BackV2.request.DistributivoRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class DistributivoServ {
    @Autowired
    private CicloAcademicoRepo cicloRepo;
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

    // Crear
    @Transactional
    public String crearDistributivo(DistributivoRequest request) {
        // Verificar si ya existe un distributivo con los mismos datos
        boolean existeDistributivo = distributivoRepo.findAll().stream().anyMatch(d ->
                d.getCiclo().getId() == request.getCicloId() &&
                        d.getAula().getId() == request.getAulaId() &&
                        d.getMateria().getId() == request.getMateriaId() &&
                        d.getDocente().getId() == docenteRepo.findByUsuarioCedula(request.getCedulaDocente()).getId()
        );
        if (existeDistributivo) throw new RuntimeException("El distributivo ya existe");

        if (validarExistenciaAulaMateria(request)) throw new RuntimeException("Ya existe un distributivo con la misma aula y materia");

        // Validaciones de existencia
        validarExistenciaCicloAulaMateriaDocente(request);

        // Verificar grado de materia y aula
        validarGradoMateriaYGradoAula(request);

        Distributivo distributivo = Distributivo.builder()
                .horasAsignadas(0)
                .ciclo(cicloRepo.findById(request.getCicloId()).get())
                .aula(aulaRepo.findById(request.getAulaId()).get())
                .materia(materiaRepo.findById(request.getMateriaId()).get())
                .docente(docenteRepo.findByUsuarioCedula(request.getCedulaDocente()))
                .build();

        distributivoRepo.save(distributivo);
        return "Distributivo creado";
    }

    // Traer todos
    public List<DistributivoDTO> obtenerDistributivos() {
        return distributivoRepo.findAll().stream()
                .map(this::convertirADTO)
                .toList();
    }

    // Traer por ID
    public DistributivoDTO obtenerDistributivo(Long id) {
        Distributivo distributivo = distributivoRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Distributivo no encontrado"));
        return convertirADTO(distributivo);
    }

    // Traer por ciclo académico
    public List<DistributivoDTO> getDistributivoByCiclo(Long id) {
        return distributivoRepo.findByCicloId(id).stream()
                .map(this::convertirADTO)
                .toList();
    }

    // Actualizar
    @Transactional
    public String editarDistributivo(DistributivoRequest request) {
        Distributivo distributivo = distributivoRepo.findById(request.getId())
                .orElseThrow(() -> new RuntimeException("Distributivo no encontrado"));

        // Verificar duplicados
        boolean duplicado = distributivoRepo.findAll().stream().anyMatch(d ->
                        d.getId() != request.getId() &&
                        d.getCiclo().getId() == request.getCicloId() &&
                        d.getAula().getId() == request.getAulaId() &&
                        d.getMateria().getId() == request.getMateriaId() &&
                        d.getDocente().getId() == docenteRepo.findByUsuarioCedula(request.getCedulaDocente()).getId()
        );
        if (duplicado) throw new RuntimeException("El distributivo ya existe");

        // Validaciones de existencia
        validarExistenciaCicloAulaMateriaDocente(request);

        // Verificar grado de materia y aula
        validarGradoMateriaYGradoAula(request);

        distributivo.setCiclo(cicloRepo.findById(request.getCicloId()).get());
        distributivo.setAula(aulaRepo.findById(request.getAulaId()).get());
        distributivo.setMateria(materiaRepo.findById(request.getMateriaId()).get());
        distributivo.setDocente(docenteRepo.findByUsuarioCedula(request.getCedulaDocente()));

        distributivoRepo.save(distributivo);
        return "Distributivo actualizado";
    }

    // Eliminar
    @Transactional
    public String eliminarDistributivo(Long id) {
        Distributivo distributivo = distributivoRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Distributivo no encontrado"));

        distributivoRepo.delete(distributivo);
        return "Distributivo eliminado";
    }

    /* --------- METODOS AUXILIARES ---------- */

    private void validarExistenciaCicloAulaMateriaDocente(DistributivoRequest request) {
        if (!cicloRepo.existsById(request.getCicloId())) throw new RuntimeException("El ciclo académico no existe");
        if (!aulaRepo.existsById(request.getAulaId())) throw new RuntimeException("El aula no existe");
        if (!materiaRepo.existsById(request.getMateriaId())) throw new RuntimeException("La materia no existe");
        if (!usuarioRepo.existsByCedula(request.getCedulaDocente()) ||
                !usuarioRepo.findByCedula(request.getCedulaDocente()).getRol().equals(Rol.DOCENTE)) {
            throw new RuntimeException("El docente no existe");
        }
    }

    private boolean validarExistenciaAulaMateria(DistributivoRequest request) {
        return distributivoRepo.existsByAulaIdAndMateriaId(request.getAulaId(), request.getMateriaId());
    }

    private void validarGradoMateriaYGradoAula(DistributivoRequest request) {
        Long gradoMateriaId = materiaRepo.findById(request.getMateriaId()).get().getGrado().getId();
        Long gradoAulaId = aulaRepo.findById(request.getAulaId()).get().getGrado().getId();
        if (!gradoMateriaId.equals(gradoAulaId)) {
            throw new RuntimeException("El grado de la materia no coincide con el grado del curso");
        }
    }

    private DistributivoDTO convertirADTO(Distributivo distributivo) {
        return DistributivoDTO.builder()
                .id(distributivo.getId())
                .cicloAcademico(distributivo.getCiclo().getNombre())
                .aula(distributivo.getAula().getParalelo())
                .grado(distributivo.getAula().getGrado().getNombre())
                .materia(distributivo.getMateria().getNombre())
                .horasSemanales(distributivo.getMateria().getHoras())
                .horasAsignadas(distributivo.getHorasAsignadas())
                .docente(distributivo.getDocente().getUsuario().getNombres() + " " + distributivo.getDocente().getUsuario().getApellidos())
                .build();
    }

}
