package com.tesis.BackV2.services.cicloacademico;

import com.tesis.BackV2.config.ApiResponse;
import com.tesis.BackV2.dto.AulaDTO;
import com.tesis.BackV2.entities.Aula;
import com.tesis.BackV2.entities.Docente;
import com.tesis.BackV2.enums.Rol;
import com.tesis.BackV2.exceptions.ApiException;
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
    public ApiResponse<String> crearAula(AulaRequest request) {
        validarParaleloYGrado(request.getParalelo(), request.getGrado());
        validarTutor(request.getCedulaTutor());

        Aula aula = Aula.builder()
                .paralelo(request.getParalelo())
                .maxEstudiantes(request.getCantEstudiantes())
                .grado(gradoRepo.findByNombre(request.getGrado()))
                .tutor(docenteRepo.findByUsuarioCedula(request.getCedulaTutor()))
                .build();

        aulaRepo.save(aula);
        return ApiResponse.<String>builder()
                .error(false)
                .mensaje("Aula creada")
                .codigo(200)
                .detalles("El aula ha sido creada correctamente")
                .build()
        ;
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
            throw new ApiException(ApiResponse.<String>builder()
                    .error(true)
                    .mensaje("Solicitud incorrecta")
                    .codigo(400)
                    .detalles("El curso no existe")
                    .build()
            );
        }
        return convertirAulaADTO(aula);
    }

    // Actualizar
    @Transactional
    public ApiResponse<String> editarAula(AulaRequest request) {
        Aula aula = aulaRepo.findById(request.getId())
                .orElseThrow(() -> new ApiException(ApiResponse.<String>builder()
                        .error(true)
                        .mensaje("Solcitud incorrecta")
                        .codigo(400)
                        .detalles("No es posible actualizar el curso, no existe")
                        .build()
                ));

        validarParaleloYGradoUnico(request.getParalelo(), request.getGrado(), request.getId());
        validarTutorParaEdicion(request.getCedulaTutor(), aula);

        aula.setParalelo(request.getParalelo());
        aula.setMaxEstudiantes(request.getCantEstudiantes());
        aula.setGrado(gradoRepo.findByNombre(request.getGrado()));
        aula.setTutor(docenteRepo.findByUsuarioCedula(request.getCedulaTutor()));

        aulaRepo.save(aula);
        return ApiResponse.<String>builder()
                .error(false)
                .mensaje("Aula actualizada")
                .codigo(200)
                .detalles("El aula ha sido actualizada correctamente")
                .build();
    }

    // Eliminar
    @Transactional
    public ApiResponse<String> eliminarAula(Long id) {
        Aula aula = aulaRepo.findById(id)
                .orElseThrow(() -> new ApiException(ApiResponse.<String>builder()
                        .error(true)
                        .mensaje("Solicitud incorrecta")
                        .codigo(400)
                        .detalles("El curso que intenta eliminar no existe")
                        .build()
                ));

        if (distributivoRepo.existsByAulaId(id)) {
            throw new ApiException(ApiResponse.<String>builder()
                    .error(true)
                    .mensaje("Distributivo asociado")
                    .codigo(400)
                    .detalles("El curso tiene distributivos asociados, no se puede eliminar")
                    .build()
            );
        }

        aulaRepo.delete(aula);
        return ApiResponse.<String>builder()
                .error(false)
                .mensaje("Aula eliminada")
                .codigo(200)
                .detalles("El aula ha sido eliminada correctamente")
                .build();
    }

    /* --------- METODOS AUXILIARES ---------- */

    private void validarParaleloYGrado(String paralelo, String grado) {
        if (aulaRepo.existsByParaleloAndGradoNombre(paralelo, grado)) {
            throw new ApiException(ApiResponse.<String>builder()
                    .error(true)
                    .mensaje("Solicitud incorrecta")
                    .codigo(400)
                    .detalles("El curso ya se encuentran registrados, no se puede crear")
                    .build()
            );
        }
    }

    private void validarParaleloYGradoUnico(String paralelo, String grado, Long idActual) {
        aulaRepo.findAll().stream()
                .filter(aula -> aula.getId() != idActual)
                .filter(aula -> aula.getParalelo().equalsIgnoreCase(paralelo) &&
                        aula.getGrado().getNombre().equalsIgnoreCase(grado))
                .findFirst()
                .ifPresent(aula -> {
                    throw new ApiException(ApiResponse.<String>builder()
                            .error(true)
                            .mensaje("Solcitud incorrecta")
                            .codigo(400)
                            .detalles("El curso ya existe, no se puede actualizar")
                            .build()
                    );
                });
    }

    private void validarTutor(String cedulaTutor) {
        var usuario = usuarioRepo.findByCedula(cedulaTutor);
        if (usuario == null || !usuario.getRol().equals(Rol.DOCENTE)) {
            throw new ApiException(ApiResponse.<String>builder()
                    .error(true)
                    .mensaje("Docente no encontrado")
                    .codigo(400)
                    .detalles("La cédula ingresada no corresponde a un docente o no existe")
                    .build()
            );
        }
        if (aulaRepo.existsByTutorId(docenteRepo.findByUsuarioCedula(cedulaTutor).getId())) {
            throw new ApiException(ApiResponse.<String>builder()
                    .error(true)
                    .mensaje("Solicitud incorrecta")
                    .codigo(400)
                    .detalles("El docente ya esta asignado como tutor, no se puede asignar a otro")
                    .build()
            );
        }
    }

    private void validarTutorParaEdicion(String cedulaTutor, Aula aulaActual) {
        Docente tutor = docenteRepo.findByUsuarioCedula(cedulaTutor);
        if (tutor == null) {
            throw new ApiException(ApiResponse.<String>builder()
                    .error(true)
                    .mensaje("Solcitud incorrecta")
                    .codigo(400)
                    .detalles("El docente no existe")
                    .build()
            );
        }
        if (aulaRepo.existsByTutorId(tutor.getId()) && aulaActual.getTutor().getId() != tutor.getId()) {
            throw new ApiException(ApiResponse.<String>builder()
                    .error(true)
                    .mensaje("Solcitud incorrecta")
                    .codigo(400)
                    .detalles("Docente ya esta asignado como tutor, no se puede asignar a otro")
                    .build()
            );
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
