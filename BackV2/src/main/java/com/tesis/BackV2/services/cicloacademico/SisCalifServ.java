package com.tesis.BackV2.services.cicloacademico;

import com.tesis.BackV2.config.ApiResponse;
import com.tesis.BackV2.entities.CicloAcademico;
import com.tesis.BackV2.entities.SistemaCalificacion;
import com.tesis.BackV2.entities.embedded.Calificacion;
import com.tesis.BackV2.enums.TipoNivel;
import com.tesis.BackV2.exceptions.ApiException;
import com.tesis.BackV2.repositories.CicloAcademicoRepo;
import com.tesis.BackV2.repositories.SistCalifRepo;
import com.tesis.BackV2.request.CalfRequest;
import com.tesis.BackV2.request.SisCalfRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class SisCalifServ {

    @Autowired
    private SistCalifRepo repo;
    @Autowired
    private CicloAcademicoRepo ciclorepo;

    // Crear
    public ApiResponse<String> crearSisCalif(SisCalfRequest request) {
        int lvl1 = 0, lvl2 = 0, lvl3 = 0, lvl4 = 0;
        Calificacion id = new Calificacion();

        // List<SistemaCalificacion> lista = repo.findAll();

        // Traer el ciclo
        CicloAcademico ciclo = ciclorepo.findById(request.getCicloID()).orElseThrow(() -> new ApiException(ApiResponse.builder()
                .error(true)
                .mensaje("Solicitud inválida")
                .codigo(404)
                .detalles("El ciclo académico especificado no existe.")
                .build()
        ));


        // Validar la cantidad de nivel 1
        if (!validarNivel1(request, ciclo)) {
            throw new ApiException(ApiResponse.builder()
                    .error(true)
                    .mensaje("Solicitud inválida")
                    .codigo(400)
                    .detalles("La cantidad de niveles 1 no coincide con la cantidad de periodos del ciclo académico.")
                    .build()
            );
        }

        // Traer el numero de registros
        long registros = repo.countSistemasByCicloId(ciclo.getId()) + 1;

        for(CalfRequest calf : request.getSistemaCalificacion()){
            // Instanciar el sistema de calificacion
            SistemaCalificacion sistema = new SistemaCalificacion();
            // Asignar el id
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
            id.setRegistro(registros);
            id.setLvl1(lvl1);
            id.setLvl2(lvl2);
            id.setLvl3(lvl3);
            id.setLvl4(lvl4);

            sistema.setId(id);
            sistema.setCiclo(ciclo);
            sistema.setTipo(calf.getTipo());
            sistema.setDescripcion(calf.getDescripcion());
            sistema.setPeso(calf.getPeso());

            repo.save(sistema);
        }

        return ApiResponse.<String>builder()
                .error(false)
                .mensaje("Sistema de calificación creado")
                .codigo(200)
                .detalles("El sistema de calificación ha sido creado correctamente.")
                .build();
    }

    // Validar la cantidad de nivel 1
    public boolean validarNivel1(SisCalfRequest request, CicloAcademico ciclo){
        int lvl1 = 0;
        int cantPeriodos = ciclo.getCantPeriodos();

        for (CalfRequest calf : request.getSistemaCalificacion()){
            if(calf.getNivel() == TipoNivel.Primero){
                lvl1++;
            }
        }

        return lvl1 == cantPeriodos;
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
