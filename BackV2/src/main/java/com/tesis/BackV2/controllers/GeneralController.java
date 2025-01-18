package com.tesis.BackV2.controllers;

import com.tesis.BackV2.config.ApiResponse;
import com.tesis.BackV2.config.jwt.JwtService;
import com.tesis.BackV2.dto.UsuarioDTO;
import com.tesis.BackV2.entities.Estudiante;
import com.tesis.BackV2.exceptions.ApiException;
import com.tesis.BackV2.exceptions.MiExcepcion;
import com.tesis.BackV2.repositories.EstudianteRepo;
import com.tesis.BackV2.repositories.MatriculaRepo;
import com.tesis.BackV2.repositories.UsuarioRepo;
import com.tesis.BackV2.services.CicloAcademicoServ;
import com.tesis.BackV2.services.UsuarioServ;
import com.tesis.BackV2.services.cicloacademico.CalendarioAcademicoServ;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/general/controller/")
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://localhost:4200"})
public class GeneralController {

    private final CicloAcademicoServ cicloAServ;
    private final JwtService jwtService;
    private final UsuarioRepo usuarioRepo;
    private final CalendarioAcademicoServ calendarioAcademicoServ;
    private final EstudianteRepo estudianteRepo;
    private final MatriculaRepo matrRepo;
    private final UsuarioServ service;
    private final UsuarioServ usuarioServ;

    // Traer todos
    @GetMapping("ciclos")
    public ResponseEntity<?> getCiclosAcademicos() {
        return ResponseEntity.ok(cicloAServ.getCiclos());
    }

    // Traer todos
    @GetMapping("grados")
    public ResponseEntity<?> getGrados() { return ResponseEntity.ok(cicloAServ.getGrados()); }


    // visualizar todos los cursos
    @GetMapping("cursos")
    public ResponseEntity<?> getCursos() {
        return ResponseEntity.ok(cicloAServ.obtenerAulas());
    }

    // visualizar cursos por grado
    @GetMapping("cursos/{grado}")
    public ResponseEntity<?> getCursosByGrado(@PathVariable String grado) {
        return ResponseEntity.ok(cicloAServ.obtenerAulasPorGrado(grado));
    }
    // Traer usuario por cedula
    @GetMapping("usuario/{cedula}")
    public ResponseEntity<UsuarioDTO> buscarUsuario(@PathVariable String cedula) throws MiExcepcion {
        return ResponseEntity.ok(service.buscarUsuario(cedula));
    }

    // Traer info del usuario autenticado
    @GetMapping("usuario")
    public ResponseEntity<UsuarioDTO> entregarUsuAuth(HttpServletRequest request){
        String token = extractTokenFromRequest(request);
        // Extraer el nombre de usuario (o cualquier dato que guardes en el token)
        String userCedula = jwtService.extractUsername(token);

        try {
            return ResponseEntity.ok(service.buscarUsuario(userCedula));
        } catch (MiExcepcion e) {
            throw new ApiException(ApiResponse.<String>builder()
                    .error(true)
                    .codigo(400)
                    .mensaje("Error de validación")
                    .detalles("Usuario no encontrado")
                    .build());
        }
    }

    // Traer estudiantes por curso
    @GetMapping("estudiantes/{idDistributivo}")
    public ResponseEntity<?> obtenerEstudiantes(@PathVariable Long idDistributivo) {
        return ResponseEntity.ok(usuarioServ.getEstudiantesByCurso(idDistributivo));
    }

    /* ----------------------------------------- DASHBOARD -----------------------------------------*/
    // Traer calendario
    @GetMapping("calendario")
    public ResponseEntity<?> obtenerCalendario() {
        return ResponseEntity.ok(calendarioAcademicoServ.obtenerCalendarioActual());
    }


    /*  ---------------------------- Visualización de Horario  ---------------------------- */
    // Traer por curso
    @GetMapping("horarios/{idCurso}")
    public ResponseEntity<?> obtenerHorarios(@PathVariable Long idCurso, HttpServletRequest request) {
        String token = extractTokenFromRequest(request);
        // Extraer el nombre de usuario (o cualquier dato que guardes en el token)
        String userCedula = jwtService.extractUsername(token);

        Estudiante estudiante = estudianteRepo.findByUsuarioCedula(userCedula);

        if (estudiante != null) {
            return ResponseEntity.ok(cicloAServ.getHorariosByCurso(matrRepo.findTopByEstudianteIdOrderByIdDesc(estudiante.getId()).getCurso().getId()));
        }

        return ResponseEntity.ok(cicloAServ.getHorariosByCurso(idCurso));
    }

    // Metodo para extraer el token del encabezado de la solicitud
    private String extractTokenFromRequest(HttpServletRequest request) {
        final String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7); // Remover el prefijo "Bearer "
        }
        throw new RuntimeException("Token no encontrado o inválido");
    }
}
