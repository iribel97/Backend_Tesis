package com.tesis.BackV2.services.cicloacademico;

import com.tesis.BackV2.config.ApiResponse;
import com.tesis.BackV2.entities.CicloAcademico;
import com.tesis.BackV2.entities.Grado;
import com.tesis.BackV2.entities.Promocion;
import com.tesis.BackV2.entities.config.InscripcionConfig;
import com.tesis.BackV2.exceptions.ApiException;
import com.tesis.BackV2.repositories.*;
import com.tesis.BackV2.repositories.config.InscripcionConfigRepo;
import com.tesis.BackV2.request.CicloARequest;

import java.time.LocalDate;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CicloServ {
    @Autowired
    private CicloAcademicoRepo cicloRepo;
    @Autowired
    private DistributivoRepo distributivoRepo;
    @Autowired
    private InscripcionConfigRepo insConfigRepo;
    @Autowired
    private PromocionRepo promocionRepo;
    @Autowired
    private GradoRepo gradoRepo;

    // Creación
    @Transactional
    public ApiResponse<String> crearCicloAcademico(CicloARequest request) {
        boolean existeConflicto = cicloRepo.findAll().stream()
                .anyMatch(ciclo ->
                        request.getFechaInicio().isBefore(ciclo.getFechaFin()) &&
                                request.getFechaFin().isAfter(ciclo.getFechaInicio())
                );

        if (existeConflicto) {
            throw new ApiException(ApiResponse.builder()
                    .error(true)
                    .mensaje("Conflicto de fechas")
                    .codigo(400)
                    .detalles("El ciclo académico tiene conflicto de fechas con otro ciclo académico.")
                    .build()
            );
        }

        CicloAcademico ciclo = CicloAcademico.builder()
                .nombre(request.getNombre())
                .cantPeriodos(request.getCantPeriodos())
                .fechaInicio(request.getFechaInicio())
                .fechaFin(request.getFechaFin())
                .inscripConfig(insConfigRepo.save(InscripcionConfig.builder()
                        .requierePruebas(request.isPruebaInscrip())
                        .fechaConfiguracion(request.getFechaInicio().atStartOfDay())
                        .build()))
                .activo(false)
                .build();

        cicloRepo.save(ciclo);

        // Crear Promoción
        Promocion prom = Promocion.builder()
                .cicloAcademico(ciclo)
                .nombre("Promoción " + convertirARomano(promocionRepo.findAll().size() + 1))
                .grado(gradoRepo.findTopByOrderByIdAsc() != null ? gradoRepo.findTopByOrderByIdAsc() : null)
                .build();

        promocionRepo.save(prom);

        // Actualizar grados para las promociones existentes
        List<Promocion> promociones = promocionRepo.findAll();
        List<Grado> grados = gradoRepo.findAll();

        int minTamano = Math.min(promociones.size(), grados.size());

        for (int i = 0; i < minTamano; i++) {
            promociones.get(promociones.size()-1-i).setGrado(grados.get(i));
            promocionRepo.save(promociones.get(promociones.size()-1-i));
        }

        return ApiResponse.<String>builder()
                .error(false)
                .mensaje("Ciclo académico creado")
                .codigo(200)
                .detalles("El ciclo académico ha sido creado correctamente.")
                .build();
    }

    // Traer todos
    public List<CicloAcademico> getCiclos() {
        return cicloRepo.findAll();
    }

    // Traer un solo por id
    public CicloAcademico getCiclo(Long id) {
        return cicloRepo.findById(id).orElseThrow(() -> new RuntimeException("Ciclo académico no encontrado"));
    }

    // Editar
    @Transactional
    public ApiResponse<String> editarCiclo(CicloARequest request) {
        CicloAcademico ciclo = cicloRepo.findById(request.getId())
                .orElseThrow(() -> new ApiException(ApiResponse.builder()
                        .error(true)
                        .mensaje("Solicitud incorrecta")
                        .codigo(400)
                        .detalles("El ciclo académico no existe.")
                        .build()
                ));

        // Validar si se está activando el ciclo
        if (request.isActivo()) {
            // Buscar el ciclo actualmente activo
            CicloAcademico cicloActivo = cicloRepo.findByActivoTrue();

            if (request.getId() != cicloActivo.getId()) {
                // Verificar que las fechas no estén dentro del rango del ciclo activo
                if (!LocalDate.now().isAfter(cicloActivo.getFechaFin())) {
                    throw new ApiException(ApiResponse.builder()
                            .error(true)
                            .mensaje("Solicitud invalida")
                            .codigo(400)
                            .detalles("No se puede activar un nuevo ciclo académico dentro del rango del ciclo activo actual.")
                            .build());
                }
                // Desactivar el ciclo actualmente activo
                cicloActivo.setActivo(false);
                cicloRepo.save(cicloActivo);
            }
        }

        ciclo.setNombre(request.getNombre());
        ciclo.setCantPeriodos(request.getCantPeriodos());
        ciclo.setFechaInicio(request.getFechaInicio());
        ciclo.setFechaFin(request.getFechaFin());
        ciclo.setActivo(ciclo.isActivo() || request.isActivo());

        cicloRepo.save(ciclo);
        return ApiResponse.<String>builder()
                .error(false)
                .mensaje("Ciclo académico actualizado")
                .codigo(200)
                .detalles("El ciclo académico ha sido actualizado correctamente.")
                .build();
    }

    // Eliminar
    @Transactional
    public ApiResponse<String> eliminarCiclo(Long id) {
        CicloAcademico ciclo = cicloRepo.findById(id).orElseThrow(() -> new RuntimeException("Ciclo académico no encontrado."));

        if (distributivoRepo.existsByCicloId(id)) {
            throw new ApiException(ApiResponse.builder()
                    .error(true)
                    .mensaje("Solicitud incorrecta")
                    .codigo(400)
                    .detalles("No es posible eliminar el ciclo académico, tiene distributivos asociados.")
                    .build()
            );
        }

        cicloRepo.delete(ciclo);
        return ApiResponse.<String>builder()
                .error(false)
                .mensaje("Ciclo académico eliminado")
                .codigo(200)
                .detalles("El ciclo académico ha sido eliminado correctamente.")
                .build();
    }

    private String convertirARomano(int numero) {

        String[] miles = {"", "M", "MM", "MMM"};
        String[] cientos = {"", "C", "CC", "CCC", "CD", "D", "DC", "DCC", "DCCC", "CM"};
        String[] decenas = {"", "X", "XX", "XXX", "XL", "L", "LX", "LXX", "LXXX", "XC"};
        String[] unidades = {"", "I", "II", "III", "IV", "V", "VI", "VII", "VIII", "IX"};

        return miles[numero / 1000] +
                cientos[(numero % 1000) / 100] +
                decenas[(numero % 100) / 10] +
                unidades[numero % 10];
    }
}
