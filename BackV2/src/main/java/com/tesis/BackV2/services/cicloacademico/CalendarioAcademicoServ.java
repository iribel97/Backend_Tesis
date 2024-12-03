package com.tesis.BackV2.services.cicloacademico;

import com.tesis.BackV2.config.ApiResponse;
import com.tesis.BackV2.dto.CalendAcademDTO;
import com.tesis.BackV2.entities.CalendarioAcademico;
import com.tesis.BackV2.entities.SistemaCalificacion;
import com.tesis.BackV2.enums.TipoNivel;
import com.tesis.BackV2.exceptions.ApiException;
import com.tesis.BackV2.repositories.CalendarioAcademicoRepo;
import com.tesis.BackV2.repositories.CicloAcademicoRepo;
import com.tesis.BackV2.repositories.SistCalifRepo;
import com.tesis.BackV2.request.CalendarioAcademicoRequest;
import com.tesis.BackV2.request.CalfRequest;
import com.tesis.BackV2.request.SisCalfRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class CalendarioAcademicoServ {

    @Autowired
    private CalendarioAcademicoRepo calendarioAcademicoRepo;
    @Autowired
    private CicloAcademicoRepo cicloAcademicoRepo;
    @Autowired
    private SistCalifRepo califRepo;

    // Crear
    @Transactional
    public ApiResponse<String> crearCalendaAcademico(CalendarioAcademicoRequest request) {
        calendarioExiste(request);
        validarFechaCalendario(request);

        CalendarioAcademico calendario = CalendarioAcademico.builder()
                .descripcion(request.getDescripcion())
                .fechaInicio(request.getFechaInicio())
                .fechaFin(request.getFechaFin())
                .ciclo(cicloAcademicoRepo.findById(request.getCicloId()).get())
                .build();

        calendarioAcademicoRepo.save(calendario);

        return ApiResponse.<String>builder()
                .error(false)
                .mensaje("Calendario académico creado con éxito.")
                .codigo(200)
                .detalles("El calendario académico se ha creado con éxito.")
                .build();
    }

    // Editar
    @Transactional
    public ApiResponse<String> editarCalendarioAcademico(CalendarioAcademicoRequest request) {
        var calendario = calendarioAcademicoRepo.findById(request.getId());

        if (calendario.isEmpty()) {
            throw new ApiException(ApiResponse.builder()
                    .error(true)
                    .mensaje("Solicitud inválida")
                    .codigo(404)
                    .detalles("El calendario académico especificado no existe.")
                    .build()
            );
        }

        calendarioExiste(request);
        validarFechaCalendario(request);

        calendario.get().setDescripcion(request.getDescripcion());
        calendario.get().setFechaInicio(request.getFechaInicio());
        calendario.get().setFechaFin(request.getFechaFin());
        calendario.get().setCiclo(cicloAcademicoRepo.findById(request.getCicloId()).get());

        calendarioAcademicoRepo.save(calendario.get());

        return ApiResponse.<String>builder()
                .error(false)
                .mensaje("Calendario académico editado con éxito.")
                .codigo(200)
                .detalles("El calendario académico se ha editado con éxito.")
                .build();
    }

    // Eliminar
    @Transactional
    public ApiResponse<String> eliminarCalendarioAcademico(long id) {
        var calendario = calendarioAcademicoRepo.findById(id);
        if (calendario.isEmpty()) {
            throw new ApiException(ApiResponse.builder()
                    .error(true)
                    .mensaje("Solicitud inválida")
                    .codigo(404)
                    .detalles("El calendario académico especificado no existe.")
                    .build()
            );
        }

        calendarioAcademicoRepo.delete(calendario.get());

        return ApiResponse.<String>builder()
                .error(false)
                .mensaje("Calendario académico eliminado con éxito.")
                .codigo(200)
                .detalles("El calendario académico se ha eliminado con éxito.")
                .build();
    }

    // Traer por ciclo académico
    public List<CalendAcademDTO> obtenerPorCicloId(long cicloId) {
        List<CalendarioAcademico> calendarios = calendarioAcademicoRepo.findAllByCicloId(cicloId);
        List<CalendAcademDTO> calendariosDTO = new ArrayList<>();

        for (CalendarioAcademico calendario : calendarios) {
            calendariosDTO.add(CalendAcademDTO.builder()
                    .id(calendario.getId())
                    .descripcion(calendario.getDescripcion())
                    .fechaInicio(calendario.getFechaInicio())
                    .fechaFin(calendario.getFechaFin())
                    .build()
            );
        }

        return calendariosDTO;
    }

    // Crear calendario académico para sistema de calificacion
    @Transactional
    public void crearCalendarioSistemaCalif (SisCalfRequest request){
        String nivelAnterior = "";
        for (CalfRequest calf : request.getSistemaCalificacion()){
            switch (calf.getNivel()) {
                case Primero -> {
                    CalendarioAcademicoRequest calendario1 = CalendarioAcademicoRequest.builder()
                            .descripcion(calf.getDescripcion())
                            .fechaInicio(calf.getFechaInicio())
                            .fechaFin(calf.getFechaFin())
                            .cicloId(request.getCicloID())
                            .build();
                    nivelAnterior = calf.getDescripcion();
                    crearCalendaAcademico(calendario1);
                }
                case Segundo -> {
                    CalendarioAcademicoRequest calendario2 = CalendarioAcademicoRequest.builder()
                            .descripcion(calf.getDescripcion() + " " + nivelAnterior)
                            .fechaInicio(calf.getFechaInicio())
                            .fechaFin(calf.getFechaFin())
                            .cicloId(request.getCicloID())
                            .build();
                    crearCalendaAcademico(calendario2);
                }
            }
        }

    }



    /*  ---------------------------- METODOS PROPIOS DEL SERVICIO ------------------------------------*/
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

    private void calendarioExiste(CalendarioAcademicoRequest request) {
        var ciclo = cicloAcademicoRepo.findById(request.getCicloId());

        if (ciclo.isEmpty()) {
            throw new ApiException(ApiResponse.builder()
                    .error(true)
                    .mensaje("Solicitud inválida")
                    .codigo(404)
                    .detalles("El ciclo académico especificado no existe.")
                    .build()
            );
        }

        boolean calendarioExiste = calendarioAcademicoRepo.existsByDescripcion(request.getDescripcion());

        if (calendarioExiste) {
            throw new ApiException(ApiResponse.builder()
                    .error(true)
                    .mensaje("Solicitud inválida")
                    .codigo(400)
                    .detalles("El calendario académico ya existe.")
                    .build()
            );
        }
    }

    // Validar que la fecha a ingresar esté entre la fecha de inicio y fin de un ciclo académico
    public void validarFechaCalendario(CalendarioAcademicoRequest request) {
        var ciclo = cicloAcademicoRepo.findById(request.getCicloId());

        if (request.getFechaInicio().isBefore(ciclo.get().getFechaInicio()) || request.getFechaInicio().isAfter(ciclo.get().getFechaFin())) {
            throw new ApiException(ApiResponse.builder()
                    .error(true)
                    .mensaje("Solicitud inválida")
                    .codigo(400)
                    .detalles("La fecha de inicio del calendario académico no está dentro del rango del ciclo académico.")
                    .build()
            );
        }

        if (request.getFechaFin().isBefore(ciclo.get().getFechaInicio()) || request.getFechaFin().isAfter(ciclo.get().getFechaFin())) {
            throw new ApiException(ApiResponse.builder()
                    .error(true)
                    .mensaje("Solicitud inválida")
                    .codigo(400)
                    .detalles("La fecha de fin del calendario académico no está dentro del rango del ciclo académico.")
                    .build()
            );
        }
    }
}
