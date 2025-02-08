package com.tesis.BackV2.config.auth;

import com.tesis.BackV2.config.ApiResponse;
import com.tesis.BackV2.config.jwt.JwtService;
import com.tesis.BackV2.entities.*;
import com.tesis.BackV2.entities.contenido.Asignacion;
import com.tesis.BackV2.entities.contenido.Entrega;
import com.tesis.BackV2.enums.EstadoEntrega;
import com.tesis.BackV2.enums.EstadoUsu;
import com.tesis.BackV2.enums.Rol;
import com.tesis.BackV2.exceptions.ApiException;
import com.tesis.BackV2.infra.MensajeHtml;
import com.tesis.BackV2.repositories.*;
import com.tesis.BackV2.repositories.contenido.AsignacionRepo;
import com.tesis.BackV2.repositories.contenido.EntregaRepo;
import com.tesis.BackV2.services.CorreoServ;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthService {
    @Autowired
    private CorreoServ emailService;

    private final MensajeHtml mensaje = new MensajeHtml();

    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    private final UsuarioRepo usuRep;
    private final DocenteRepo docRep;
    private final EstudianteRepo estRep;
    private final RepresentanteRepo repRep;
    private final CicloAcademicoRepo cicloRep;
    private final InscripcionRepo insRep;
    private final MatriculaRepo matrRep;
    private final DistributivoRepo distRep;
    private final AsignacionRepo asigRep;
    private final EntregaRepo entreRep;
    private final PromocionRepo promRep;

    public ApiResponse<String> register(RegisterRequest request, Rol rol, EstadoUsu estado) {

        // comprobar si el usuario ya existe
        if (usuRep.existsByCedula(request.getCedula())) {
            throw new ApiException(ApiResponse.builder()
                    .error(true)
                    .mensaje("Solicitud inválida")
                    .codigo(400)
                    .detalles("El usuario ya existe.")
                    .build()
            );
        }

        // Crear instancia de usuario
        Usuario usuario = Usuario.builder()
                .cedula(request.getCedula())
                .password(passwordEncoder.encode(request.getPassword() == null ? request.getCedula() : request.getPassword()))
                .nombres(request.getNombres())
                .apellidos(request.getApellidos())
                .email(request.getCorreo())
                .telefono(request.getTelefono())
                .nacimiento(request.getNacimiento())
                .genero(request.getGenero())
                .rol(rol)
                .estado(estado)
                .direccion(request.getDireccion())
                .creacion(LocalDate.now())
                .build();

        // Guardar usuario
        usuRep.save(usuario);

        switch (rol) {
            case REPRESENTANTE:
                crearYGuardarRepresentante(request, usuario);
                break;
            case ESTUDIANTE:
                break;
            case DOCENTE:
                crearYGuardarDocente(request, usuario);
                break;
            case ADMIN, AOPERACIONAL:
                break;
            default:
                throw new ApiException(ApiResponse.builder()
                        .error(true)
                        .mensaje("Solicitud inválida")
                        .codigo(400)
                        .detalles("Rol no válido.")
                        .build()
                );
        }

        // contraseña vacia y que el correo no contenga la palabra "example"
        if (request.getPassword() == null && !request.getCorreo().contains("example")) {

            crearMensaje(usuario.getEmail(), "Creación de Cuenta", mensaje.mensajeCreacionCuenta(usuario.getApellidos() + " " + usuario.getNombres(), usuario.getCedula(), usuario.getCedula()));

        }

        return ApiResponse.<String>builder()
                .error(false)
                .mensaje("Usuario registrado con éxito.")
                .codigo(200)
                .build();
    }

    public void registerEstudiante(String cedula, Rol rol, EstadoUsu estado) {

        Inscripcion inscripcion = insRep.findById(cedula).orElseThrow(() -> new ApiException(ApiResponse.builder()
                .error(true)
                .mensaje("Solicitud inválida")
                .codigo(400)
                .detalles("La inscripción no existe.")
                .build()
        ));

        // validar si el usuario ya existe
        if (usuRep.existsByCedula(inscripcion.getCedula())) {
            throw new ApiException(ApiResponse.builder()
                    .error(true)
                    .mensaje("Solicitud inválida")
                    .codigo(400)
                    .detalles("El usuario ya existe.")
                    .build()
            );
        }

        Usuario usuario = Usuario.builder()
                .cedula(inscripcion.getCedula())
                .password(passwordEncoder.encode(inscripcion.getCedula()))
                .nombres(inscripcion.getNombres())
                .apellidos(inscripcion.getApellidos())
                .email(inscripcion.getEmail())
                .telefono(inscripcion.getTelefono())
                .nacimiento(inscripcion.getFechaNacimiento())
                .genero(inscripcion.getGenero())
                .rol(rol)
                .estado(estado)
                .direccion(inscripcion.getDireccion())
                .creacion(LocalDate.now())
                .build();

        if (!usuario.getEmail().contains("example")) {

            crearMensaje(usuario.getEmail(), "Creación de Cuenta", mensaje.mensajeCreacionCuenta(usuario.getApellidos() + " " + usuario.getNombres(), usuario.getCedula(), usuario.getCedula()));

        }

        usuRep.save(usuario);

        crearYGuardarEstudiante(inscripcion, usuario);
    }

    // cambiar contraseña
    public void cambiarContraUsuario(Usuario usuario) {

        usuario.setPassword(passwordEncoder.encode(usuario.getCedula()));
        usuario.setEstado(EstadoUsu.Inactivo);
        crearMensaje(usuario.getEmail(), "Habilitar cuenta", mensaje.mensajeActivarCuenta(usuario.getApellidos() + " " + usuario.getNombres(), usuario.getCedula(), usuario.getCedula()));
        usuRep.save(usuario);

    }

    public ApiResponse<?> cambiarPassUsuario (String passActual, String passUsu, String cedula) {
        Usuario usuario = usuRep.findByCedula(cedula);

        if (!passwordEncoder.matches(passActual, usuario.getPassword())) {
            throw new ApiException(ApiResponse.builder()
                    .error(true)
                    .mensaje("Solicitud inválida")
                    .codigo(400)
                    .detalles("La contraseña actual no coincide.")
                    .build()
            );
        }

        usuario.setPassword(passwordEncoder.encode(passUsu));
        if (usuario.getEstado().equals(EstadoUsu.Inactivo)) {
            usuario.setEstado(EstadoUsu.Activo);
        }
        usuRep.save(usuario);

        return ApiResponse.<String> builder()
                .error(false)
                .codigo(200)
                .mensaje("Cambio de estado correcto")
                .detalles("Se cambió el estado de la cuenta exitosamente")
                .build();
    }


    private void crearYGuardarDocente(RegisterRequest request, Usuario usuario) {
        Docente docente = Docente.builder()
                .usuario(usuario)
                .titulo(request.getTitulo())
                .especialidad(request.getEspecialidad())
                .experiencia(request.getExperiencia())
                .build();
        docRep.save(docente);
    }

    private void crearYGuardarEstudiante(Inscripcion request, Usuario usuario) {
        // traer fecha actual
        LocalDate fechaActual = LocalDate.now();
        // traer ciclo activo
        CicloAcademico ciclo = cicloRep.findByActivoTrue();

        Representante representante = repRep.findByUsuarioCedula(request.getRepresentante().getUsuario().getCedula());
        if (representante == null) {
            throw new ApiException(ApiResponse.builder()
                    .error(true)
                    .mensaje("Solicitud inválida")
                    .codigo(400)
                    .detalles("El representante no existe.")
                    .build()
            );
        }
        Estudiante estudiante = Estudiante.builder()
                .usuario(usuario)
                .ingreso(LocalDate.now())
                .representante(representante)
                .promocion(promRep.findTopByGradoNombreOrderByIdDesc(request.getGrado().getNombre()))
                .build();

        Matricula matricula = matrRep.findTopByInscripcionCedulaOrderByIdDesc(request.getCedula());
        matricula.setEstudiante(estRep.save(estudiante));

        Matricula matriculaAct = matrRep.save(matricula);

        // si la fecha actual se encuentra dentro del rango del ciclo
        if (fechaActual.isAfter(ciclo.getFechaInicio()) && fechaActual.isBefore(ciclo.getFechaFin())) {
            // traer el distributivo por id del ciclo y el id del curso
            Collection<Distributivo> distributivos = distRep.findByCicloIdAndCursoId(ciclo.getId(), matriculaAct.getCurso().getId());
             for ( Distributivo dis : distributivos) {
                 // traer las asignaciones por id del distributivo
                 List<Asignacion> asignaciones = asigRep.findByTema_Unidad_Distributivo_Id(dis.getId());

                 // si asignaciones no esta vacio
                    if (!asignaciones.isEmpty()) {
                        for (Asignacion asig : asignaciones) {
                            // crear entrega
                            entreRep.save(Entrega.builder()
                                            .estudiante(matriculaAct.getEstudiante())
                                            .activo(asig.isActivo())
                                            .asignacion(asig)
                                            .estado(EstadoEntrega.Pendiente)
                                    .build());
                        }
                    }
             }
        }

    }

    private void crearYGuardarRepresentante(RegisterRequest request, Usuario usuario) {
        Representante representante = Representante.builder()
                .usuario(usuario)
                .ocupacion(request.getOcupacion())
                .empresa(request.getEmpresa())
                .direccion(request.getDireccionEmpresa())
                .telefono(request.getTelefonoEmpresa())
                .build();
        repRep.save(representante);
    }

    public void crearMensaje (String destinatario, String asunto, String contenidoHtml) {
        try {
            emailService.enviarCorreo(destinatario, asunto, contenidoHtml);
        } catch (Exception e) {
            throw new ApiException(ApiResponse.builder()
                    .error(true)
                    .mensaje("Error al enviar el correo.")
                    .codigo(500)
                    .detalles(e.getMessage())
                    .build()
            );
        }
    }
    // -------------------------------------------------------------------------------------

    public AuthResponse login(LoginRequest request) {
        try {
            // Autenticar credenciales
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getCedula(), request.getPassword()));
        } catch (Exception e) {
            // Lanza excepción personalizada si las credenciales son incorrectas
            throw new ApiException(ApiResponse.builder()
                    .error(true)
                    .mensaje("Credenciales incorrectas.")
                    .codigo(401) // Código de error para autenticación fallida
                    .detalles("Por favor, verifica tu cédula y contraseña.")
                    .build());
        }

        // Buscar usuario
        UserDetails user = usuRep.findByCedula(request.getCedula());
        if (user == null) {
            throw new ApiException(ApiResponse.builder()
                    .error(true)
                    .mensaje("Usuario no encontrado.")
                    .codigo(404) // Código de error para usuario no encontrado
                    .detalles("El usuario con la cédula proporcionada no existe.")
                    .build());
        }

        // Generar token
        String token = jwtService.generateToken(user);

        return AuthResponse.builder()
                .token(token)
                .estadoUsuario(String.valueOf(usuRep.findByCedula(request.getCedula()).getEstado()))
                .rolUsuario(cambiarRol(usuRep.findByCedula(request.getCedula()).getRol()))
                .build();
    }

    // cambiar rol de Est a String
    private String cambiarRol(Rol rol){
        switch (rol){
            case ADMIN:
                return "Admin";
            case AOPERACIONAL:
                return "Institucional";
            case DOCENTE:
                return "Docente";
            case ESTUDIANTE:
                return "Estudiante";
            case REPRESENTANTE:
                return "Representante";
            default:
                return "ERROR";
        }
    }


}
