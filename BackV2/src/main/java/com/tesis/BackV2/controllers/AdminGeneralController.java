package com.tesis.BackV2.controllers;

import com.tesis.BackV2.config.ApiResponse;
import com.tesis.BackV2.config.auth.AuthService;
import com.tesis.BackV2.config.auth.RegisterRequest;
import com.tesis.BackV2.dto.UsuarioDTO;
import com.tesis.BackV2.entities.Grado;
import com.tesis.BackV2.enums.EstadoUsu;
import com.tesis.BackV2.enums.Rol;
import com.tesis.BackV2.exceptions.MiExcepcion;
import com.tesis.BackV2.request.CicloARequest;
import com.tesis.BackV2.request.DistributivoRequest;
import com.tesis.BackV2.request.MateriaRequest;
import com.tesis.BackV2.request.UsuarioEditRequest;
import com.tesis.BackV2.services.UsuarioServ;
import com.tesis.BackV2.services.cicloacademico.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/")
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://localhost:4200"})
public class AdminGeneralController {

    private final UsuarioServ service;
    private final AuthService authServ;
    private final CicloServ cicloServ;
    private final GradoServ gradoServ;
    private final AulaServ aulaServ;
    private final MateriaServ materiaServ;
    private final DistributivoServ distributivoServ;
    private final HorarioServ horarioServ;

    /* ---------------------------- GESTION DE USUARIOS ---------------------------- */

    // Traer usuarios
    @GetMapping("usuarios")
    public ResponseEntity<?> getUsuarios() {
        return ResponseEntity.ok(service.getUsuarios());
    }

    // Traer usuario por cedula
    @GetMapping("usuario/{cedula}")
    public ResponseEntity<UsuarioDTO> buscarUsuario(@PathVariable String cedula) throws MiExcepcion {
        return ResponseEntity.ok(service.buscarUsuario(cedula));
    }

    // Registrar administrador general
    @PostMapping("registro/admin")
    public ResponseEntity<ApiResponse<?>> registerAdmin(@RequestBody RegisterRequest registerRequest) {
        return ResponseEntity.ok(authServ.register(registerRequest, Rol.ADMIN, EstadoUsu.Inactivo));
    }

    // Registrar administrador operacional
    @PostMapping("registro/adminOp")
    public ResponseEntity<ApiResponse<?>> registerAdminOp(@RequestBody RegisterRequest registerRequest) {
        return ResponseEntity.ok(authServ.register(registerRequest, Rol.AOPERACIONAL, EstadoUsu.Inactivo));
    }

    // Registar docente
    @PostMapping("registro/docente")
    public ResponseEntity<ApiResponse<?>> registerDocente(@RequestBody RegisterRequest registerRequest) {
        return ResponseEntity.ok(authServ.register(registerRequest, Rol.DOCENTE, EstadoUsu.Inactivo));
    }

    // Editar estado usuarios
    @PutMapping("usuario")
    public ResponseEntity<ApiResponse<?>> editarUsuario(@RequestBody UsuarioEditRequest request) {
        // try catch para capturar la escepcion en caso que el request no sea un UsuarioEditRequest
        try {
            return ResponseEntity.ok(service.editarEstado(request));
        } catch (RuntimeException e) {
            return ResponseEntity.ok(ApiResponse.<String>builder()
                    .error(true)
                    .codigo(400)
                    .mensaje("Solicitud invalida")
                    .detalles(e.getMessage())
                    .build()

            );
        }
    }

    // Eliminar usuario
    @DeleteMapping("usuario/{cedula}")
    public ResponseEntity<ApiResponse<?>> eliminarUsuario(@PathVariable String cedula) {
        return ResponseEntity.ok(service.eliminarUsuario(cedula));
    }

    /* ---------------------------- GESTION CICLO ACADEMICO ----------------------------*/
    // Crear
    @PostMapping("ciclo")
    public ResponseEntity<ApiResponse<?>> crearCicloAcademico(@RequestBody CicloARequest request) {
        return ResponseEntity.ok(cicloServ.crearCicloAcademico(request));
    }

    // Traer todos
    @GetMapping("ciclo")
    public ResponseEntity<?> getCiclosAcademicos() {
        return ResponseEntity.ok(cicloServ.getCiclos());
    }

    // Traer un solo por id
    @GetMapping("ciclo/{id}")
    public ResponseEntity<?> getCicloAcademico(@PathVariable Long id) {
        return ResponseEntity.ok(cicloServ.getCiclo(id));
    }

    //Actualizar
    @PutMapping("ciclo")
    public ResponseEntity<ApiResponse<?>> actualizarCicloAcademico(@RequestBody CicloARequest request) {
        return ResponseEntity.ok(cicloServ.editarCiclo(request));
    }

    // Eliminar
    @DeleteMapping("ciclo/{id}")
    public ResponseEntity<ApiResponse<?>> eliminarCicloAcademico(@PathVariable Long id) {
        return ResponseEntity.ok(cicloServ.eliminarCiclo(id));
    }

    /* ---------------------------- GESTIÓN GRADO ACADEMICO ---------------------------- */
    // Crear
    @PostMapping("grado")
    public ResponseEntity<ApiResponse<?>> crearGrado(@RequestBody Grado request) {
        return ResponseEntity.ok(gradoServ.crearGrado(request));
    }

    // Traer todos
    @GetMapping("grados")
    public ResponseEntity<?> getGrados() { return ResponseEntity.ok(gradoServ.getGrados()); }

    // Traer grado por nombre
    @GetMapping("grado/{nombre}")
    public ResponseEntity<?> getGrado(@PathVariable String nombre) {
        return ResponseEntity.ok(gradoServ.getGrado(nombre));
    }

    // Actualizar
    @PutMapping("grado")
    public ResponseEntity<ApiResponse<?>> actualizarGrado(@RequestBody Grado request) {
        return ResponseEntity.ok(gradoServ.editarGrado(request));
    }

    // Eliminar
    @DeleteMapping("grado/{id}")
    public ResponseEntity<ApiResponse<?>> eliminarGrado(@PathVariable Long id) {
        return ResponseEntity.ok(gradoServ.eliminarGrado(id));
    }

    /* ---------------------------- GESTIÓN MATERIAS ---------------------------- */
    // Crear
    @PostMapping("materia")
    public ResponseEntity<ApiResponse<?>> crearMateria(@RequestBody MateriaRequest request) {
        return ResponseEntity.ok(materiaServ.crearMateria(request));
    }

    // Traer todas
    @GetMapping("materias")
    public ResponseEntity<?> getMaterias() {
        return ResponseEntity.ok(materiaServ.getMaterias());
    }

    // Traer por id
    @GetMapping("materia/{id}")
    public ResponseEntity<?> getMateria(@PathVariable Long id) {
        return ResponseEntity.ok(materiaServ.getMateria(id));
    }

    // Actualizar
    @PutMapping("materia")
    public ResponseEntity<ApiResponse<?>> actualizarMateria(@RequestBody MateriaRequest request) {
        return ResponseEntity.ok(materiaServ.editarMateria(request));
    }

    // Eliminar
    @DeleteMapping("materia/{id}")
    public ResponseEntity<ApiResponse<?>> eliminarMateria(@PathVariable Long id) {
        return ResponseEntity.ok(materiaServ.eliminarMateria(id));
    }

    /* ---------------------------- GESTIÓN DISTRIBUTIVO ---------------------------- */
    // Crear
    @PostMapping("distributivo")
    public ResponseEntity<ApiResponse<?>> crearDistributivo(@RequestBody DistributivoRequest request) {
        return ResponseEntity.ok(distributivoServ.crearDistributivo(request));
    }

    // Traer todos
    @GetMapping("distributivo")
    public ResponseEntity<?> getDistributivos() {
        return ResponseEntity.ok(distributivoServ.obtenerDistributivos());
    }

    // Traer por id
    @GetMapping("distributivo/{id}")
    public ResponseEntity<?> getDistributivo(@PathVariable Long id) {
        return ResponseEntity.ok(distributivoServ.obtenerDistributivo(id));
    }

    // Traer por id del ciclo académico
    @GetMapping("distributivo/ciclo/{id}")
    public ResponseEntity<?> getDistributivoByCiclo(@PathVariable Long id) {
        return ResponseEntity.ok(distributivoServ.getDistributivoByCiclo(id));
    }

    // Actualizar
    @PutMapping("distributivo")
    public ResponseEntity<ApiResponse<?>> actualizarDistributivo(@RequestBody DistributivoRequest request) {
        return ResponseEntity.ok(distributivoServ.editarDistributivo(request));
    }

    // Eliminar
    @DeleteMapping("distributivo/{id}")
    public ResponseEntity<ApiResponse<?>> eliminarDistributivo(@PathVariable Long id) {
        return ResponseEntity.ok(distributivoServ.eliminarDistributivo(id));
    }
}
