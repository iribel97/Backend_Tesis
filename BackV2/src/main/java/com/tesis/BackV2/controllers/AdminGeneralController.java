package com.tesis.BackV2.controllers;

import com.tesis.BackV2.config.ApiResponse;
import com.tesis.BackV2.config.auth.AuthService;
import com.tesis.BackV2.config.auth.RegisterRequest;
import com.tesis.BackV2.entities.Grado;
import com.tesis.BackV2.entities.config.HorarioConfig;
import com.tesis.BackV2.enums.EstadoUsu;
import com.tesis.BackV2.enums.Rol;
import com.tesis.BackV2.request.*;
import com.tesis.BackV2.services.CicloAcademicoServ;
import com.tesis.BackV2.services.UsuarioServ;
import com.tesis.BackV2.services.cicloacademico.*;
import com.tesis.BackV2.services.config.HorarioConfigServ;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/")
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://192.168.2.149:4200"})
public class AdminGeneralController {

    private final CicloAcademicoServ cicloAServ;
    private final UsuarioServ service;
    private final AuthService authServ;
    private final SisCalifServ sisCalifServ;
    private final CalendarioAcademicoServ calendarioServ;
    private final HorarioConfigServ horarioConfigServ;

    /* ---------------------------- DASHBOARD ---------------------------------------*/
    // info docentes
    @GetMapping("dashboard/docentes")
    public ResponseEntity<?> infoDocentes() {
        return ResponseEntity.ok(cicloAServ.cantDocentesFaltAsig());
    }

    // cantidad de usuarios
    @GetMapping("dashboard/catidad/usuarios/rol")
    public ResponseEntity<?> cantidadUsuariosPorRol() {
        return ResponseEntity.ok(service.cantidadUsuariosTotal());
    }

    // traer ciclo activo
    @GetMapping("dashboard/ciclo/activo")
    public ResponseEntity<?> getCicloActivo() {
        return ResponseEntity.ok(cicloAServ.getCicloActivo());
    }

    // cantidad de estudiantes por grado
    @GetMapping("dashboard/estudiantes/grado")
    public ResponseEntity<?> cantidadEstudiantesPorGrado() {
        return ResponseEntity.ok(cicloAServ.obtenerCantidadesEstudiantes());
    }

    /* ---------------------------- GESTION DE USUARIOS ---------------------------- */

    // Traer usuarios
    @GetMapping("usuarios")
    public ResponseEntity<?> getUsuarios() {
        return ResponseEntity.ok(service.getUsuarios());
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

    // Eliminar usuario
    @DeleteMapping("usuario/{cedula}")
    public ResponseEntity<ApiResponse<?>> eliminarUsuario(@PathVariable String cedula) {
        return ResponseEntity.ok(service.eliminarUsuario(cedula));
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

    // Traer docentes
    @GetMapping("docentes")
    public ResponseEntity<?> getDocentes() {
        return ResponseEntity.ok(service.getDocentes());
    }

    /* ---------------------------- GESTION CICLO ACADEMICO ----------------------------*/
    // Crear
    @PostMapping("ciclo")
    public ResponseEntity<ApiResponse<?>> crearCicloAcademico(@RequestBody CicloARequest request) {
        return ResponseEntity.ok(cicloAServ.crearCicloAcademico(request));
    }

    // Traer un solo por id
    @GetMapping("ciclo/{id}")
    public ResponseEntity<?> getCicloAcademico(@PathVariable Long id) {
        return ResponseEntity.ok(cicloAServ.getCiclo(id));
    }

    //Actualizar
    @PutMapping("ciclo")
    public ResponseEntity<ApiResponse<?>> actualizarCicloAcademico(@RequestBody CicloARequest request) {
        return ResponseEntity.ok(cicloAServ.editarCiclo(request));
    }

    // Eliminar
    @DeleteMapping("ciclo/{id}")
    public ResponseEntity<ApiResponse<?>> eliminarCicloAcademico(@PathVariable Long id) {
        return ResponseEntity.ok(cicloAServ.eliminarCiclo(id));
    }

    /* ---------------------------- GESTIÓN GRADO ACADEMICO ---------------------------- */
    // Crear
    @PostMapping("grado")
    public ResponseEntity<ApiResponse<?>> crearGrado(@RequestBody Grado request) {
        return ResponseEntity.ok(cicloAServ.crearGrado(request));
    }

    // Traer grado por nombre
    @GetMapping("grado/{nombre}")
    public ResponseEntity<?> getGrado(@PathVariable String nombre) {
        return ResponseEntity.ok(cicloAServ.getGrado(nombre));
    }

    // Actualizar
    @PutMapping("grado")
    public ResponseEntity<ApiResponse<?>> actualizarGrado(@RequestBody Grado request) {
        return ResponseEntity.ok(cicloAServ.editarGrado(request));
    }

    // Eliminar
    @DeleteMapping("grado/{id}")
    public ResponseEntity<ApiResponse<?>> eliminarGrado(@PathVariable Long id) {
        return ResponseEntity.ok(cicloAServ.eliminarGrado(id));
    }

    /* ---------------------------- GESTIÓN MATERIAS ---------------------------- */
    // Crear
    @PostMapping("materia")
    public ResponseEntity<ApiResponse<?>> crearMateria(@RequestBody MateriaRequest request) {
        return ResponseEntity.ok(cicloAServ.crearMateria(request));
    }

    // Traer todas
    @GetMapping("materias")
    public ResponseEntity<?> getMaterias() {
        return ResponseEntity.ok(cicloAServ.getMaterias());
    }

    // Traer por id
    @GetMapping("materia/{id}")
    public ResponseEntity<?> getMateria(@PathVariable Long id) {
        return ResponseEntity.ok(cicloAServ.getMateria(id));
    }

    // Actualizar
    @PutMapping("materia")
    public ResponseEntity<ApiResponse<?>> actualizarMateria(@RequestBody MateriaRequest request) {
        return ResponseEntity.ok(cicloAServ.editarMateria(request));
    }

    // Eliminar
    @DeleteMapping("materia/{id}")
    public ResponseEntity<ApiResponse<?>> eliminarMateria(@PathVariable Long id) {
        return ResponseEntity.ok(cicloAServ.eliminarMateria(id));
    }

    // visualizar materias por grado
    @GetMapping("materias/{grado}")
    public ResponseEntity<?> getMateriasByGrado(@PathVariable String grado) {
        return ResponseEntity.ok(cicloAServ.getMateriasPorGrado(grado));
    }



    /* ---------------------------- GESTIÓN DISTRIBUTIVO ---------------------------- */
    // Crear
    @PostMapping("distributivo")
    public ResponseEntity<ApiResponse<?>> crearDistributivo(@RequestBody DistributivoRequest request) {
        return ResponseEntity.ok(cicloAServ.crearDistributivo(request));
    }

    // Traer por id
    @GetMapping("distributivo/{id}")
    public ResponseEntity<?> getDistributivo(@PathVariable Long id) {
        return ResponseEntity.ok(cicloAServ.obtenerDistributivo(id));
    }

    // traer por ciclo y curso
    @GetMapping("distributivo/{cicloId}/curso/{cursoId}")
    public ResponseEntity<?> getDistributivoByCicloCurso(@PathVariable Long cicloId, @PathVariable Long cursoId) {
        return ResponseEntity.ok(cicloAServ.getDistributivoByCicloAndCurso(cicloId, cursoId));
    }

    // traer por ciclo y docente
    @GetMapping("distributivo/{cicloId}/{cedula}")
    public ResponseEntity<?> getDistributivoByCicloDocente(@PathVariable Long cicloId, @PathVariable String cedula) {
        return ResponseEntity.ok(cicloAServ.getDistributivoByCicloAndDocente(cicloId, cedula));
    }

    // traer por ciclo
    @GetMapping("distributivo/ciclo/{cicloId}")
    public ResponseEntity<?> getDistributivoByCiclo(@PathVariable Long cicloId) {
        return ResponseEntity.ok(cicloAServ.getDistributivoByCiclo(cicloId));
    }

    // Actualizar
    @PutMapping("distributivo")
    public ResponseEntity<ApiResponse<?>> actualizarDistributivo(@RequestBody DistributivoRequest request) {
        return ResponseEntity.ok(cicloAServ.editarDistributivo(request));
    }

    // Eliminar
    @DeleteMapping("distributivo/{id}")
    public ResponseEntity<ApiResponse<?>> eliminarDistributivo(@PathVariable Long id) {
        return ResponseEntity.ok(cicloAServ.eliminarDistributivo(id));
    }

    /* ---------------------------- VISUALIZACIÓN DE HORARIOS ---------------------------- */

    // Crear la configuración del horario
    @PostMapping("/horario/config")
    public ResponseEntity<ApiResponse<?>> crearHorarioConfig(@RequestBody HorarioConfig request) {
        return ResponseEntity.ok(horarioConfigServ.crearHorarioConfig(request));
    }

    // Editar
    @PutMapping("/horario/config")
    public ResponseEntity<ApiResponse<?>> editarHorarioConfig(@RequestBody HorarioConfig request) {
        return ResponseEntity.ok(horarioConfigServ.editarHorarioConfig(request));
    }

    // Listar todos
    @GetMapping("/horarios")
    public ResponseEntity<?> horarios() {
        return ResponseEntity.ok(horarioConfigServ.horarios());
    }

    // Eliminar
    @DeleteMapping("/horario/config/{id}")
    public ResponseEntity<ApiResponse<?>> eliminarHorarioConfig(@PathVariable Long id) {
        return ResponseEntity.ok(horarioConfigServ.eliminarHorarioConfig(id));
    }

    /* ---------------------------- GESTIÓN SISTEMA CALIFICACIONES ---------------------------- */
    // Crear
    @PostMapping("calificacion")
    public ResponseEntity<ApiResponse<?>> crearSistemaCalificacion(@RequestBody SisCalfRequest request) {
        calendarioServ.crearCalendarioSistemaCalif(request);
        return ResponseEntity.ok(sisCalifServ.crearSisCalif(request));
    }

    // Editar
    @PutMapping("calificacion")
    public ResponseEntity<ApiResponse<?>> editarSistemaCalificacion(@RequestBody SisCalfRequest request) {
        calendarioServ.editarCalendarioSistemaCalif(request);
        return ResponseEntity.ok(sisCalifServ.editarSisCalif(request));
    }

    // Eliminar
    @DeleteMapping("calificacion/{cicloId}/{registro}")
    public ResponseEntity<ApiResponse<?>> eliminarSistemaCalificacion(@PathVariable Long cicloId, @PathVariable Long registro) {
        return ResponseEntity.ok(sisCalifServ.eliminarSisCalif(cicloId, registro));
    }

    // Traer por ciclo
    @GetMapping("calificacion/ciclo/{cicloId}")
    public ResponseEntity<?> getSistemaCalificacionByCiclo(@PathVariable Long cicloId) {
        return ResponseEntity.ok(sisCalifServ.traerPorCiclo(cicloId));
    }

    /* ---------------------------- GESTIÓN CALENDARIO ACADEMICO ---------------------------- */
    // Crear
    @PostMapping("calendario")
    public ResponseEntity<ApiResponse<?>> crearCalendarioAcademico(@RequestBody CalendarioAcademicoRequest request) {
        return ResponseEntity.ok(calendarioServ.crearCalendaAcademico(request));
    }

    // Editar
    @PutMapping("calendario")
    public ResponseEntity<ApiResponse<?>> editarCalendarioAcademico(@RequestBody CalendarioAcademicoRequest request) {
        return ResponseEntity.ok(calendarioServ.editarCalendarioAcademico(request));
    }

    // Eliminar
    @DeleteMapping("calendario/{id}")
    public ResponseEntity<ApiResponse<?>> eliminarCalendarioAcademico(@PathVariable Long id) {
        return ResponseEntity.ok(calendarioServ.eliminarCalendarioAcademico(id));
    }

    // Traer por ciclo académico
    @GetMapping("calendario/ciclo/{cicloId}")
    public ResponseEntity<?> obtenerPorCicloId(@PathVariable Long cicloId) {
        return ResponseEntity.ok(calendarioServ.obtenerPorCicloId(cicloId));
    }
}
