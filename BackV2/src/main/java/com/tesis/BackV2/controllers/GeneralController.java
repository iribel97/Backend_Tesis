package com.tesis.BackV2.controllers;

import com.tesis.BackV2.config.jwt.JwtService;
import com.tesis.BackV2.entities.Estudiante;
import com.tesis.BackV2.entities.Usuario;
import com.tesis.BackV2.repositories.EstudianteRepo;
import com.tesis.BackV2.repositories.UsuarioRepo;
import com.tesis.BackV2.services.UsuarioServ;
import com.tesis.BackV2.services.cicloacademico.HorarioServ;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/general/controller/")
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://localhost:4200"})
public class GeneralController {

    private final HorarioServ horarioServ;
    private final JwtService jwtService;
    private final UsuarioRepo usuarioRepo;
    private final EstudianteRepo estudianteRepo;


    /*  ---------------------------- Visualización de Horario  ---------------------------- */
    // Traer por curso
    @GetMapping("horarios/{idCurso}")
    public ResponseEntity<?> obtenerHorarios(@PathVariable Long idCurso, HttpServletRequest request) {
        String token = extractTokenFromRequest(request);
        // Extraer el nombre de usuario (o cualquier dato que guardes en el token)
        String userCedula = jwtService.extractUsername(token);

        Estudiante estudiante = estudianteRepo.findByUsuarioCedula(userCedula);

        if (estudiante != null) {
            return ResponseEntity.ok(horarioServ.getHorariosByCurso(estudiante.getMatricula().getCurso().getId()));
        }

        return ResponseEntity.ok(horarioServ.getHorariosByCurso(idCurso));
    }

    // Método para extraer el token del encabezado de la solicitud
    private String extractTokenFromRequest(HttpServletRequest request) {
        final String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7); // Remover el prefijo "Bearer "
        }
        throw new RuntimeException("Token no encontrado o inválido");
    }
}
