package com.tesis.BackV2.services.cicloacademico;

import com.tesis.BackV2.dto.AulaDTO;
import com.tesis.BackV2.entities.Aula;
import com.tesis.BackV2.entities.Docente;
import com.tesis.BackV2.enums.Rol;
import com.tesis.BackV2.repositories.*;
import com.tesis.BackV2.request.AulaRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AulaServ {

    @Autowired
    private GradoRepo gradoRepo;
    @Autowired
    private AulaRepo aulaRepo;
    @Autowired
    private DocenteRepo docenteRepo;
    @Autowired
    private UsuarioRepo usuarioRepo;
    @Autowired
    private DistributivoRepo distributivoRepo;


    // Creación
    @Transactional
    public String crearAula(AulaRequest request) {
        validarParaleloYGrado(request.getParalelo(), request.getGrado());
        validarTutor(request.getCedulaTutor());

        Aula aula = Aula.builder()
                .paralelo(request.getParalelo())
                .maxEstudiantes(request.getCantEstudiantes())
                .grado(gradoRepo.findByNombre(request.getGrado()))
                .tutor(docenteRepo.findByUsuarioCedula(request.getCedulaTutor()))
                .build();

        aulaRepo.save(aula);
        return "Creación de aula exitosa";
    }

    // Traer todas
    public List<AulaDTO> obtenerAulas() {
        return aulaRepo.findAll().stream()
                .map(this::convertirAulaADTO)
                .toList();
    }

    // Traer solo uno
    public AulaDTO obtenerAula(String paralelo, String grado) {
        Aula aula = aulaRepo.findByParaleloAndGradoNombre(paralelo, grado);
        if (aula == null) {
            throw new RuntimeException("Aula no encontrada");
        }
        return convertirAulaADTO(aula);
    }

    // Actualizar
    @Transactional
    public String editarAula(AulaRequest request) {
        Aula aula = aulaRepo.findById(request.getId())
                .orElseThrow(() -> new RuntimeException("Aula no encontrada"));

        validarParaleloYGradoUnico(request.getParalelo(), request.getGrado(), request.getId());
        validarTutorParaEdicion(request.getCedulaTutor(), aula);

        aula.setParalelo(request.getParalelo());
        aula.setMaxEstudiantes(request.getCantEstudiantes());
        aula.setGrado(gradoRepo.findByNombre(request.getGrado()));
        aula.setTutor(docenteRepo.findByUsuarioCedula(request.getCedulaTutor()));

        aulaRepo.save(aula);
        return "Actualización completa";
    }

    // Eliminar
    @Transactional
    public String eliminarAula(Long id) {
        Aula aula = aulaRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Aula no encontrada"));

        if (distributivoRepo.existsByAulaId(id)) {
            throw new RuntimeException("No se puede eliminar el aula porque tiene distributivos asociados");
        }

        aulaRepo.delete(aula);
        return "Aula eliminada";
    }

    /* --------- METODOS AUXILIARES ---------- */

    private void validarParaleloYGrado(String paralelo, String grado) {
        if (aulaRepo.existsByParaleloAndGradoNombre(paralelo, grado)) {
            throw new RuntimeException("El paralelo ya existe");
        }
    }

    private void validarParaleloYGradoUnico(String paralelo, String grado, Long idActual) {
        aulaRepo.findAll().stream()
                .filter(aula -> aula.getId() != idActual)
                .filter(aula -> aula.getParalelo().equalsIgnoreCase(paralelo) &&
                        aula.getGrado().getNombre().equalsIgnoreCase(grado))
                .findFirst()
                .ifPresent(aula -> {
                    throw new RuntimeException("El paralelo ya existe");
                });
    }

    private void validarTutor(String cedulaTutor) {
        var usuario = usuarioRepo.findByCedula(cedulaTutor);
        if (usuario == null || !usuario.getRol().equals(Rol.DOCENTE)) {
            throw new RuntimeException("El tutor no existe o no es un docente");
        }
        if (aulaRepo.existsByTutorId(docenteRepo.findByUsuarioCedula(cedulaTutor).getId())) {
            throw new RuntimeException("El tutor ya tiene un aula asignada");
        }
    }

    private void validarTutorParaEdicion(String cedulaTutor, Aula aulaActual) {
        Docente tutor = docenteRepo.findByUsuarioCedula(cedulaTutor);
        if (tutor == null) {
            throw new RuntimeException("El tutor no existe");
        }
        if (aulaRepo.existsByTutorId(tutor.getId()) && aulaActual.getTutor().getId() != tutor.getId()) {
            throw new RuntimeException("El tutor ya tiene un aula asignada");
        }
    }

    private AulaDTO convertirAulaADTO(Aula aula) {
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
}
