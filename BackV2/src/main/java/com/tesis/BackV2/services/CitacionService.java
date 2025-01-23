package com.tesis.BackV2.services;

import com.tesis.BackV2.config.ApiResponse;
import com.tesis.BackV2.dto.CitacionDTO;
import com.tesis.BackV2.entities.Citacion;
import com.tesis.BackV2.entities.Estudiante;
import com.tesis.BackV2.infra.MensajeHtml;
import com.tesis.BackV2.repositories.CicloAcademicoRepo;
import com.tesis.BackV2.repositories.CitacionRepo;
import com.tesis.BackV2.repositories.DocenteRepo;
import com.tesis.BackV2.repositories.EstudianteRepo;
import com.tesis.BackV2.request.CitacionRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CitacionService {

    @Autowired
    private CorreoServ emailService;

    private final MensajeHtml mensaje = new MensajeHtml();

    private final CitacionRepo repo;
    private final EstudianteRepo estudianteRepo;
    private final DocenteRepo docenteRepo;
    private final CicloAcademicoRepo cicloRepo;

    // Crear citación
    @Transactional
    public ApiResponse<?> crearCitacion(CitacionRequest request, String cedulaDocente) {
        Estudiante est = estudianteRepo.findByUsuarioCedula(request.getCedulaEst());
        Citacion citacion = Citacion.builder()
                .fecha(request.getFecha())
                .hora(request.getHora())
                .motivo(request.getMotivo())
                .observaciones(request.getObservaciones())
                .estudiante(est)
                .docente(docenteRepo.findByUsuarioCedula(cedulaDocente))
                .confirmada(false)
                .representante(est.getRepresentante())
                .cicloAcademico(cicloRepo.findByActivoTrue())
                .build();

        repo.save(citacion);

        // Convert LocalDate and LocalTime to String
        String fechaStr = citacion.getFecha().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        String horaStr = request.getHora().format(DateTimeFormatter.ofPattern("HH:mm"));


        // Enviar correo
        emailService.enviarCorreo(
                citacion.getRepresentante().getUsuario().getEmail(),
                "Citación",
                mensaje.mensajeCitacionDocenteRep(
                        citacion.getRepresentante().getUsuario().getApellidos() + " " + citacion.getRepresentante().getUsuario().getNombres(),
                        citacion.getEstudiante().getUsuario().getApellidos() + " " + citacion.getEstudiante().getUsuario().getNombres(),
                        citacion.getDocente().getUsuario().getApellidos() + " " + citacion.getDocente().getUsuario().getNombres(),
                        fechaStr,
                        horaStr,
                        request.getMotivo(),
                        request.getObservaciones()
                )
        );

        return ApiResponse.<String> builder()
                .error(false)
                .codigo(200)
                .mensaje("Solicitud exitosa")
                .detalles("Citación creada exitosamente")
                .build();
    }

    // Mostrar citaciones por docente
    public List<CitacionDTO> citacionByDocente (long idDocente){

        List<Citacion> citaciones = repo.findByDocenteIdAndConfirmadaFalse(idDocente);

        return citaciones.stream()
            .sorted(Comparator.comparing(Citacion::getFecha).thenComparing(Citacion::getHora))
            .map(this::convertirDTO)
            .toList();
    }

    // cambiar el estado de la citacion
    @Transactional
    public ApiResponse<?> cambiarEstadoCitacion(long idCitacion){
        Citacion citacion = repo.findById( idCitacion).orElse(null);
        if (citacion == null) {
            return ApiResponse.<String>builder()
                    .error(true)
                    .codigo(400)
                    .mensaje("Error de validación")
                    .detalles("Citación no encontrada")
                    .build();
        }

        citacion.setConfirmada(true);
        repo.save(citacion);

        return ApiResponse.<String>builder()
                .error(false)
                .codigo(200)
                .mensaje("Solicitud exitosa")
                .detalles("Citación confirmada")
                .build();
    }


    /* ---------------------- METODOS PRIVADOS DEL SERVICIO ---------------------------- */
    private CitacionDTO convertirDTO ( Citacion citacion){
        return CitacionDTO.builder()
                .id(citacion.getId())
                .fecha(citacion.getFecha())
                .hora(citacion.getHora())
                .motivo(citacion.getMotivo())
                .observaciones(citacion.getObservaciones())
                .nombreRepresentante(citacion.getRepresentante().getUsuario().getApellidos() + " " + citacion.getRepresentante().getUsuario().getNombres())
                .nombreAlumno(citacion.getEstudiante().getUsuario().getApellidos() + " " + citacion.getEstudiante().getUsuario().getNombres())
                .build();
    }
}
