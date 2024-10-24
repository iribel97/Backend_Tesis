package com.tesis.BackV2.services;

import com.tesis.BackV2.entities.Docente;
import com.tesis.BackV2.entities.Estudiante;
import com.tesis.BackV2.entities.Usuario;
import com.tesis.BackV2.enums.Genero;
import com.tesis.BackV2.enums.Rol;
import com.tesis.BackV2.exceptions.MiExcepcion;
import com.tesis.BackV2.repositories.DocenteRepo;
import com.tesis.BackV2.repositories.EstudianteRepo;
import com.tesis.BackV2.repositories.UsuarioRepo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class UsuarioServ {

    @Autowired
    private UsuarioRepo repoU;
    @Autowired
    private DocenteRepo repoD;
    @Autowired
    private EstudianteRepo repoE;

    /* ----- CRUD DE LA ENTIDAD USUARIO ----- */
    //Crear un usuario de tipo Docente
    @Transactional
    public void crearUsuarioDocente(Usuario usuario, Docente datosD) throws MiExcepcion {
        // Verificar que el usuario no exista
        validarUsuarioNoExistente(usuario.getCedula());

        // Asignar valores al objeto Docente
        datosD.setUsuario(usuario);
        // Encriptar la contraseña
        usuario.setPassword(new BCryptPasswordEncoder().encode(usuario.getPassword()));

        // Guardar el objeto Usuario
        repoU.save(usuario);
        // Guardar el objeto Docente
        repoD.save(datosD);
    }

    //Crear un usuario de tipo Estudiante
    @Transactional
    public void crearUsuarioEstudiante(Usuario usuario, LocalDate fechaIngreso, String tipoSangre, Usuario representante) throws MiExcepcion {
        // Verificar que el usuario no exista
        validarUsuarioNoExistente(usuario.getCedula());

        // Instancian objeto de tipo Estudiante
        Estudiante estudiante = new Estudiante();

        // Asignar valores al objeto Estudiante
        estudiante.setUsuario(usuario);
        estudiante.setIngreso(fechaIngreso);
        estudiante.setSangre(tipoSangre);
        estudiante.setRepresentante(representante);
        // Encriptar la contraseña
        usuario.setPassword(new BCryptPasswordEncoder().encode(usuario.getPassword()));

        // Guardar el objeto Usuario
        repoU.save(usuario);

        // Guardar el objeto Estudiante
        repoE.save(estudiante);
    }

    //Retornar todos los usuarios
    public List<Usuario> listarUsuarios() {
        return repoU.findAll();
    }

    //Retornar un usuario por su cédula
    public Usuario listUsuId(String cedula) throws MiExcepcion {
        if (!repoU.existsById(cedula)) {
            throw new MiExcepcion("El usuario con cédula " + cedula + " no existe.");
        }
        return repoU.findByCedula(cedula);
    }

    public Docente listDocId(String cedula) throws MiExcepcion {
        if (!repoU.findByCedula(cedula).getRol().equals(Rol.DOCENTE)) {
            throw new MiExcepcion("El docente con cédula " + cedula + " no existe.");
        }
        return repoD.findByUsuarioCedula(cedula);
    }

    // Retornar los usuarios de tipo Docente
    public List<Docente> listarDocentes() {
        return repoD.findAll();
    }

    // Retornar los usuarios de tipo Estudiante
    public List<Estudiante> listarEstudiantes() {
        return repoE.findAll();
    }

    /* ---------- VALIDACIONES ----------- */
    private void validarUsuarioNoExistente(String cedula) throws MiExcepcion {
        if (repoU.existsById(cedula)) {
            throw new MiExcepcion("El usuario con cédula " + cedula + " ya existe.");
        }
    }

}
