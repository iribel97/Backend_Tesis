package com.tesis.BackV2.config.auth;

import com.tesis.BackV2.config.ApiResponse;
import com.tesis.BackV2.config.jwt.JwtService;
import com.tesis.BackV2.entities.*;
import com.tesis.BackV2.enums.EstadoUsu;
import com.tesis.BackV2.enums.Rol;
import com.tesis.BackV2.exceptions.ApiException;
import com.tesis.BackV2.infra.MensajeHtml;
import com.tesis.BackV2.repositories.*;
import com.tesis.BackV2.services.CorreoServ;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
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
    private final InscripcionRepo insRep;

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
            String username = request.getApellidos() + " " + request.getNombres();
            String destinatario = request.getCorreo();
            String asunto = "Creación de Cuenta";
            String contenidoHtml = mensaje.mensajeCreacionCuenta(username, request.getCedula(), request.getCedula());

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

        return ApiResponse.<String>builder()
                .error(false)
                .mensaje("Usuario registrado con éxito.")
                .codigo(200)
                .build();
    }

    // Registrar lista de usuarios
    public ApiResponse<String> registerList(List<RegisterRequest> requests, Rol rol, EstadoUsu estado) {
        for (RegisterRequest request : requests) {
            register(request, rol, estado);
        }
        return ApiResponse.<String>builder()
                .error(false)
                .mensaje("Usuarios registrados con éxito.")
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
            String username = usuario.getApellidos() + " " + usuario.getNombres();
            String destinatario = usuario.getEmail();
            String asunto = "Creación de Cuenta";
            String contenidoHtml = mensaje.mensajeCreacionCuenta(username, usuario.getCedula(), usuario.getCedula());

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

        usuRep.save(usuario);

        crearYGuardarEstudiante(inscripcion, usuario);
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
                .build();
        estRep.save(estudiante);
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
    // -------------------------------------------------------------------------------------

    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getCedula(), request.getPassword()));
        UserDetails user= usuRep.findByCedula(request.getCedula());
        String token = jwtService.generateToken(user);
        return AuthResponse.builder()
                .token(token)
                .estadoUsuario(String.valueOf(usuRep.findByCedula(request.getCedula()).getEstado()))
                .build();
    }

}
