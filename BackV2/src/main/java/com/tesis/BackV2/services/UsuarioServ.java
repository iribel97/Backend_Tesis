package com.tesis.BackV2.services;

import com.tesis.BackV2.dto.DocenteDTO;
import com.tesis.BackV2.dto.EstudianteDTO;
import com.tesis.BackV2.dto.UsuarioDTO;
import com.tesis.BackV2.entities.Docente;
import com.tesis.BackV2.entities.Estudiante;
import com.tesis.BackV2.entities.Usuario;
import com.tesis.BackV2.enums.Genero;
import com.tesis.BackV2.enums.Rol;
import com.tesis.BackV2.exceptions.MiExcepcion;
import com.tesis.BackV2.repositories.DocenteRepo;
import com.tesis.BackV2.repositories.EstudianteRepo;
import com.tesis.BackV2.repositories.UsuarioRepo;

import com.tesis.BackV2.request.UsuarioRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class UsuarioServ {

    @Autowired
    private UsuarioRepo repoU;
    @Autowired
    private DocenteRepo repoD;
    @Autowired
    private EstudianteRepo repoE;

    /* ----- CRUD DE LA ENTIDAD USUARIO ----- */

    //Actualizar usuario
    @Transactional
    public String actualizarUser(UsuarioRequest request) throws MiExcepcion {
        Usuario usuario = repoU.findByCedula(request.getCedula());

        //Validar que el usuario exista
        if (usuario != null && usuario.getRol().equals(request.getRol())) {
            //Actualizar los datos del usuario generales del usuario
            Usuario usu = Usuario.builder()
                    .cedula(request.getCedula())
                    .nombres(request.getNombres())
                    .apellidos(request.getApellidos())
                    .email(request.getCorreo())
                    .telefono(request.getTelefono())
                    .nacimiento(request.getNacimiento())
                    .genero(request.getGenero())
                    .rol(request.getRol())
                    .estado(request.getEstado())
                    .direccion(request.getDireccion())
                    .build();
            repoU.save(usu);

            //Actualizar los datos del docente
            if (request.getRol().equals(Rol.DOCENTE)) {

                Docente docente = repoD.findById(request.getId()).orElseThrow(() -> new MiExcepcion("El docente con cédula " + request.getCedula() + " no existe."));
                docente.setTitulo(request.getTitulo());
                docente.setEspecialidad(request.getEspecialidad());
                docente.setExperiencia(request.getExperiencia());

                repoD.save(docente);
            }

            //Actualizar los datos del estudiante
            if (request.getRol().equals(Rol.ESTUDIANTE)) {

                Estudiante estudiante = repoE.findById(request.getId()).orElseThrow(() -> new MiExcepcion("El estudiante con cédula " + request.getCedula() + " no existe."));
                estudiante.setIngreso(request.getIngreso());
                estudiante.setRepresentante(repoU.findByCedula(request.getCedulaRepresentante()));

                repoE.save(estudiante);
            }

        } else {
            return "El usuario con cédula " + request.getCedula() + " no existe.";
        }

        return "Usuario actualizado";
    }

    public UsuarioDTO buscarUsuario(String cedula) throws MiExcepcion {
        Usuario usuario = repoU.findById(cedula).orElseThrow(() -> new MiExcepcion("El usuario con cédula " + cedula + " no existe."));

        Docente docente = repoD.findByUsuarioCedula(cedula);
        Estudiante estudiante = repoE.findByUsuarioCedula(cedula);

        DocenteDTO docenteDTO = docente != null ? DocenteDTO.builder()
                .id(docente.getId())
                .titulo(docente.getTitulo())
                .especialidad(docente.getEspecialidad())
                .experiencia(docente.getExperiencia())
                .build() : null;

        EstudianteDTO estudianteDTO = estudiante != null ? EstudianteDTO.builder()
                .id(estudiante.getId())
                .ingreso(estudiante.getIngreso())
                .representante(buscarUsuario(estudiante.getRepresentante().getCedula())) 
                .build() : null;

        return UsuarioDTO.builder()
                .cedula(usuario.getCedula())
                .nombres(usuario.getNombres())
                .apellidos(usuario.getApellidos())
                .correo(usuario.getEmail())
                .telefono(usuario.getTelefono())
                .direccion(usuario.getDireccion())
                .nacimiento(usuario.getNacimiento())
                .genero(usuario.getGenero())
                .rol(usuario.getRol())
                .estado(usuario.getEstado())
                .docente(docenteDTO)
                .estudiante(estudianteDTO)
                .build();

    }

    //Retornar un estudiante
    public Estudiante buscarEstudiante(String cedula){
        return repoE.findByUsuarioCedula(cedula);
    }

    //Retornar un docente
    public Docente buscarDocente(String cedula){
        return repoD.findByUsuarioCedula(cedula);
    }



    /* ---------- VALIDACIONES ----------- */
    private void validarUsuarioNoExistente(String cedula) throws MiExcepcion {
        if (repoU.existsById(cedula)) {
            throw new MiExcepcion("El usuario con cédula " + cedula + " ya existe.");
        }
    }

}
