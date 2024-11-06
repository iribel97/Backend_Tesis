package com.tesis.BackV2.config.auth;

import com.tesis.BackV2.config.jwt.JwtService;
import com.tesis.BackV2.entities.Docente;
import com.tesis.BackV2.entities.Estudiante;
import com.tesis.BackV2.entities.Representante;
import com.tesis.BackV2.entities.Usuario;
import com.tesis.BackV2.enums.Rol;
import com.tesis.BackV2.repositories.DocenteRepo;
import com.tesis.BackV2.repositories.EstudianteRepo;
import com.tesis.BackV2.repositories.RepresentanteRepo;
import com.tesis.BackV2.repositories.UsuarioRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    private final UsuarioRepo usuRep;
    private final DocenteRepo docRep;
    private final EstudianteRepo estRep;
    private final RepresentanteRepo repRep;

    public AuthResponse register(RegisterRequest request) {
        // Comprobar que el usuario ya existe
        if (usuRep.existsByCedula(request.getCedula())) {
            throw new RuntimeException("El usuario ya existe");
        }

        Usuario usuario = buildUsuario(request);
        usuRep.save(usuario);

        switch (request.getRol()) {
            case DOCENTE:
                crearYGuardarDocente(request, usuario);
                break;
            case ESTUDIANTE:
                crearYGuardarEstudiante(request, usuario);
                break;
            case REPRESENTANTE:
                crearYGuardarRepresentante(request, usuario);
                break;
            case ADMIN:
                break;
            default:
                throw new RuntimeException("Rol no reconocido");
        }

        return AuthResponse.builder()
                .token(jwtService.generateToken(usuario))
                .build();
    }

    // -------------------------------------------------------------------------------------
    private Usuario buildUsuario(RegisterRequest request) {
        byte[] foto = null;

        if (request.getFoto() != null && !request.getFoto().isEmpty()){
            try {
                foto = request.getFoto().getBytes();
            } catch (IOException e) {
                throw new RuntimeException("Error al procesar la foto de perfil", e);
            }
        }


        return Usuario.builder()
                .cedula(request.getCedula())
                .password(passwordEncoder.encode(request.getPassword()))
                .nombres(request.getNombre())
                .apellidos(request.getApellido())
                .email(request.getCorreo())
                .telefono(request.getTelefono())
                .nacimiento(request.getNacimiento())
                .genero(request.getGenero())
                .rol(request.getRol())
                .estado(request.getEstado())
                .direccion(request.getDireccion())
                .creacion(LocalDate.now())
                .foto(foto)
                .tipo(request.getFoto() != null ? request.getFoto().getContentType() : null)
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

    private void crearYGuardarEstudiante(RegisterRequest request, Usuario usuario) {
        Representante representante = repRep.findByUsuarioCedula(request.getCedulaRepresentante());
        if (representante == null) {
            throw new RuntimeException("Representante no encontrado");
        }
        Estudiante estudiante = Estudiante.builder()
                .usuario(usuario)
                .ingreso(request.getIngreso())
                .representante(representante)
                .build();
        estRep.save(estudiante);
    }

    private void crearYGuardarRepresentante(RegisterRequest request, Usuario usuario) {
        Representante representante = Representante.builder()
                .usuario(usuario)
                .autorizado(request.isAutorizado())
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
                .build();
    }
}
