package com.tesis.BackV2.config.auth;

import com.tesis.BackV2.config.jwt.JwtService;
import com.tesis.BackV2.entities.Docente;
import com.tesis.BackV2.entities.Estudiante;
import com.tesis.BackV2.entities.Usuario;
import com.tesis.BackV2.enums.Rol;
import com.tesis.BackV2.repositories.DocenteRepo;
import com.tesis.BackV2.repositories.EstudianteRepo;
import com.tesis.BackV2.repositories.UsuarioRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    private final UsuarioRepo usuRep;
    private final DocenteRepo docRep;
    private final EstudianteRepo estRep;

    //Metodo para el registro de un usuario
    public AuthResponse register(RegisterRequest request){
        // Creación de un nuevo usuario base
        Usuario usuario = Usuario.builder()
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
                .build();
        usuRep.save(usuario); // Guardar el usuario en la base de datos tipo admin o tipo representante

        // Si el usuario es un docente
        if(request.getRol().equals(Rol.DOCENTE)){
            // Creación de un nuevo docente
            Docente docente = Docente.builder()
                    .usuario(usuario)
                    .titulo(request.getTitulo())
                    .especialidad(request.getEspecialidad())
                    .experiencia(request.getExperiencia())
                    .build();
            docRep.save(docente); // Guardar el docente en la base de datos
        }

        // Si el usuario es un estudiante
        if(request.getRol().equals(Rol.ESTUDIANTE)){
            // Creación de un nuevo estudiante
            Estudiante estudiante = Estudiante.builder()
                    .usuario(usuario)
                    .ingreso(request.getIngreso())
                    .representante(usuRep.findByCedula(request.getCedulaRepresentante()))
                    .build();
            estRep.save(estudiante); // Guardar el estudiante en la base de datos
        }

        return AuthResponse.builder()
                .token(jwtService.generateToken(usuario))
                .build();
    }

    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getCedula(), request.getPassword()));
        UserDetails user= usuRep.findByCedula(request.getCedula());
        String token = jwtService.generateToken(user);
        return AuthResponse.builder()
                .token(token)
                .build();
    }
}
