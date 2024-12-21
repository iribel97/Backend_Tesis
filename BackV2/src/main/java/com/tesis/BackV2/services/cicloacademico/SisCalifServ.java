package com.tesis.BackV2.services.cicloacademico;

import com.tesis.BackV2.config.ApiResponse;
import com.tesis.BackV2.dto.SistCalfDTO;
import com.tesis.BackV2.entities.CalendarioAcademico;
import com.tesis.BackV2.entities.CicloAcademico;
import com.tesis.BackV2.entities.SistemaCalificacion;
import com.tesis.BackV2.entities.embedded.Calificacion;
import com.tesis.BackV2.enums.TipoNivel;
import com.tesis.BackV2.enums.TipoSistCalif;
import com.tesis.BackV2.exceptions.ApiException;
import com.tesis.BackV2.repositories.CalendarioAcademicoRepo;
import com.tesis.BackV2.repositories.CicloAcademicoRepo;
import com.tesis.BackV2.repositories.SistCalifRepo;
import com.tesis.BackV2.request.CalfRequest;
import com.tesis.BackV2.request.SisCalfRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class SisCalifServ {

    @Autowired
    private SistCalifRepo repo;
    @Autowired
    private CicloAcademicoRepo ciclorepo;
    @Autowired
    private CalendarioAcademicoRepo calendariorepo;

    // Crear
    @Transactional
    public ApiResponse<String> crearSisCalif(SisCalfRequest request) {
        int lvl1 = 0, lvl2 = 0, lvl3 = 0, lvl4 = 0;
        long cant1, cant2, cant3;
        Calificacion id = new Calificacion();

        // Traer el ciclo
        CicloAcademico ciclo = traerCiclo(request.getCicloID());

        // Validar la cantidad de nivel 1
        validarNivel1(request, ciclo);

        // Traer el numero de registros
        long registros = repo.countSistemasByCicloId(ciclo.getId()) + 1;


        // Map para contar los elementos de cada nivel con tipo 'Porcentual'
        Map<TipoNivel, Long> countPorcentualByNivel = new HashMap<>();

        // Primero, contar cuántos elementos de tipo 'Porcentual' hay para cada nivel
        for(CalfRequest calf : request.getSistemaCalificacion()) {
            if(!calf.getNivel().equals(TipoNivel.Cuarto)) {
                if (calf.getTipo().equals(TipoSistCalif.Porcentual)) {
                    countPorcentualByNivel.put(calf.getNivel(), countPorcentualByNivel.getOrDefault(calf.getNivel(), 0L) + 1);
                }
            }
        }

        cant1 = countPorcentualByNivel.getOrDefault(TipoNivel.Primero, 0L);
        cant2 = countPorcentualByNivel.getOrDefault(TipoNivel.Segundo, 0L);
        cant3 = countPorcentualByNivel.getOrDefault(TipoNivel.Tercero, 0L);


        for(CalfRequest calf : request.getSistemaCalificacion()){
            double peso = 0;
            // Instanciar el sistema de calificacion
            SistemaCalificacion sistema = new SistemaCalificacion();
            // Asignar el id
            switch (calf.getNivel()){
                   case Primero:
                       lvl1++;
                       lvl2 = lvl3 = lvl4 = 0;
                       peso = (double) 100 / cant1;
                       break;
                   case Segundo:
                       lvl2++;
                       lvl3 = lvl4 = 0;
                       peso = (double) 100 / ((double) cant2 /cant1);
                       break;
                   case Tercero:
                       lvl3++;
                       lvl4 = 0;
                       peso = (double) 100 / ((double) cant3 /cant2);
                       break;
                   case Cuarto:
                       lvl4++;
                       break;
            }

            id.setRegistro(registros);
            id.setLvl1(lvl1);
            id.setLvl2(lvl2);
            id.setLvl3(lvl3);
            id.setLvl4(lvl4);

            sistema.setId(id);
            sistema.setCiclo(ciclo);
            sistema.setDescripcion(calf.getDescripcion());
            if (!calf.getNivel().equals(TipoNivel.Cuarto)) {
                sistema.setTipo(calf.getTipo());
                sistema.setPeso(String.valueOf(peso));
                sistema.setFechaInicio(calf.getFechaInicio());
                sistema.setFechaFin(calf.getFechaFin());
            }
            sistema.setBase(calf.getBase());


            repo.save(sistema);
        }

        return ApiResponse.<String>builder()
                .error(false)
                .mensaje("Sistema de calificación creado")
                .codigo(200)
                .detalles("El sistema de calificación ha sido creado correctamente.")
                .build();
    }

    // Editar sistema de calificacion
    @Transactional
    public ApiResponse<String> editarSisCalif(SisCalfRequest request) {

        // Traer registros por ciclo y por registro
        List<SistemaCalificacion> sistemas = repo.findByCicloIdAndIdRegistro(request.getCicloID(), request.getRegistro());

        if (sistemas.isEmpty()) {
            throw new ApiException(ApiResponse.builder()
                    .error(true)
                    .mensaje("Solicitud inválida")
                    .codigo(404)
                    .detalles("El sistema de calificación especificado no existe.")
                    .build()
            );
        }


        for (SistemaCalificacion sistema : sistemas) {
            int lvl1 = 0, lvl2 = 0, lvl3 = 0, lvl4 = 0;
            // Actualizar el sistema de calificacion
            for (CalfRequest calf : request.getSistemaCalificacion()) {
                switch (calf.getNivel()){
                    case Primero:
                        lvl1++;
                        lvl2 = lvl3 = lvl4 = 0;
                        break;
                    case Segundo:
                        lvl2++;
                        lvl3 = lvl4 = 0;
                        break;
                    case Tercero:
                        lvl3++;
                        lvl4 = 0;
                        break;
                    case Cuarto:
                        lvl4++;
                        break;
                }

                if(lvl1 == sistema.getId().getLvl1() && lvl2 == sistema.getId().getLvl2() && lvl3 == sistema.getId().getLvl3() && lvl4 == sistema.getId().getLvl4()){

                    sistema.setTipo(calf.getTipo());
                    sistema.setPeso(calf.getPeso());
                    sistema.setFechaInicio(calf.getFechaInicio());
                    sistema.setFechaFin(calf.getFechaFin());
                    repo.save(sistema);

                }

            }

        }

        return ApiResponse.<String>builder()
                .error(false)
                .mensaje("Sistema de calificación editado")
                .codigo(200)
                .detalles("El sistema de calificación ha sido editado correctamente.")
                .build();
    }

    // Eliminar sistema de calificacion
    @Transactional
    public ApiResponse<String> eliminarSisCalif(Long cicloId, Long registro) {
        // Traer registros por ciclo y por registro
        List<SistemaCalificacion> sistemas = repo.findByCicloIdAndIdRegistro(cicloId, registro);

        if (sistemas.isEmpty()) {
            throw new ApiException(ApiResponse.builder()
                    .error(true)
                    .mensaje("Solicitud inválida")
                    .codigo(404)
                    .detalles("El sistema de calificación especificado no existe.")
                    .build()
            );
        }

        repo.eliminarPorCicloIdYRegistro(cicloId, registro);

        return ApiResponse.<String>builder()
                .error(false)
                .mensaje("Sistema de calificación eliminado")
                .codigo(200)
                .detalles("El sistema de calificación ha sido eliminado correctamente.")
                .build();
    }

    // Traer por ciclo
    public List<SistCalfDTO> traerPorCiclo(Long cicloId){
        List<SistemaCalificacion> sistemas = repo.findByCicloId(cicloId);

        if (sistemas.isEmpty()) {
            throw new ApiException(ApiResponse.builder()
                    .error(true)
                    .mensaje("Solicitud inválida")
                    .codigo(404)
                    .detalles("No se encontraron sistemas de calificación para el ciclo especificado.")
                    .build()
            );
        }

        return sistemas.stream().map(sistema -> SistCalfDTO.builder()
                .ciclo(sistema.getCiclo().getNombre())
                .nivel(niveles(sistema))
                .peso(sistema.getPeso())
                .tipo(sistema.getTipo())
                .fechaInicio(sistema.getFechaInicio())
                .fechaFin(sistema.getFechaFin())
                .build()
        ).collect(Collectors.toList());

    }

    /* ---------------------- METODOS PROPIOS DEL SERVICIO ---------------------------------- */
    private CicloAcademico traerCiclo(Long id){
        return ciclorepo.findById(id).orElseThrow(() -> new ApiException(ApiResponse.builder()
                .error(true)
                .mensaje("Solicitud inválida")
                .codigo(404)
                .detalles("El ciclo académico especificado no existe.")
                .build()
        ));
    }

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


    // Validar la cantidad de nivel 1
    private void validarNivel1(SisCalfRequest request, CicloAcademico ciclo){
        int lvl1 = 0;
        int cantPeriodos = ciclo.getCantPeriodos();

        for (CalfRequest calf : request.getSistemaCalificacion()){
            if(calf.getNivel() == TipoNivel.Primero){
                lvl1++;
            }
        }

        if(lvl1 != cantPeriodos){
            throw new ApiException(ApiResponse.builder()
                    .error(true)
                    .mensaje("Solicitud inválida")
                    .codigo(400)
                    .detalles("La cantidad de periodos no coincide con la cantidad de niveles 1.")
                    .build()
            );
        }
    }

    // Validar el peso por nivel
    private boolean validarPesoNivel(List<CalfRequest> request) {
        double sumaPorcentual = 0, sumaNumerico = 0;

        for (CalfRequest calf : request) {
            if (calf.getNivel() == TipoNivel.Primero) {
                switch (calf.getTipo()) {
                    case Porcentual -> sumaPorcentual += Double.parseDouble(calf.getPeso());
                    case Numerico -> sumaNumerico += Double.parseDouble(calf.getPeso());
                    case Cualitativo -> {
                        return true; // Cualitativo no requiere validación
                    }
                }
            }
        }

        // Ajustar escala para pesos numéricos si es necesario
        sumaNumerico = sumaNumerico >= 10 ? sumaNumerico * 100 : sumaNumerico;

        return sumaPorcentual == 100 || sumaNumerico == 100;
    }

}
