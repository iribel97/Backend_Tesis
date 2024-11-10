package com.tesis.BackV2.services;

import com.tesis.BackV2.dto.DocenteDTO;
import com.tesis.BackV2.dto.EstudianteDTO;
import com.tesis.BackV2.dto.RepresentanteDTO;
import com.tesis.BackV2.dto.UsuarioDTO;
import com.tesis.BackV2.entities.Docente;
import com.tesis.BackV2.entities.Estudiante;
import com.tesis.BackV2.entities.Representante;
import com.tesis.BackV2.entities.Usuario;
import com.tesis.BackV2.enums.Genero;
import com.tesis.BackV2.enums.Rol;
import com.tesis.BackV2.exceptions.MiExcepcion;
import com.tesis.BackV2.repositories.DocenteRepo;
import com.tesis.BackV2.repositories.EstudianteRepo;
import com.tesis.BackV2.repositories.RepresentanteRepo;
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
    @Autowired
    private RepresentanteRepo repoR;

    /* ----- CRUD DE LA ENTIDAD USUARIO ----- */

    //Actualizar datos de un usuario (sin contar password, estado)
    @Transactional
    public String actualizarUser(UsuarioRequest request) throws MiExcepcion {
        Usuario usuario = repoU.findByCedula(request.getCedula());

        //Validar que el usuario exista
        if (usuario != null ) {
            //Actualizar los datos del usuario generales del usuario
            usuario.setNombres(request.getNombres());
            usuario.setApellidos(request.getApellidos());
            usuario.setEmail(request.getCorreo());
            usuario.setTelefono(request.getTelefono());
            usuario.setDireccion(request.getDireccion());
            usuario.setNacimiento(request.getNacimiento());
            usuario.setGenero(request.getGenero());

            repoU.save(usuario);

            switch (usuario.getRol()){
                case DOCENTE:
                    Docente docente = repoD.findByUsuarioCedula(request.getCedula());
                    docente.setTitulo(request.getTitulo());
                    docente.setEspecialidad(request.getEspecialidad());
                    docente.setExperiencia(request.getExperiencia());
                    repoD.save(docente);
                    break;
                case ESTUDIANTE:
                    Estudiante estudiante = repoE.findByUsuarioCedula(request.getCedula());
                    estudiante.setIngreso(request.getIngreso());
                    estudiante.setRepresentante(repoR.findByUsuarioCedula(request.getCedulaRepresentante()));
                    repoE.save(estudiante);
                    break;
                case REPRESENTANTE:
                    Representante representante = repoR.findByUsuarioCedula(request.getCedula());
                    representante.setAutorizado(request.isAutorizado());
                    representante.setOcupacion(request.getOcupacion());
                    representante.setEmpresa(request.getEmpresa());
                    representante.setDireccion(request.getDireccionEmpresa());
                    representante.setTelefono(request.getTelefonoEmpresa());
                    repoR.save(representante);
                    break;
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
        Representante representante = repoR.findByUsuarioCedula(cedula);

        DocenteDTO docenteDTO = docente != null ? DocenteDTO.builder()
                .id(docente.getId())
                .titulo(docente.getTitulo())
                .especialidad(docente.getEspecialidad())
                .experiencia(docente.getExperiencia())
                .build() : null;

        EstudianteDTO estudianteDTO = estudiante != null ? EstudianteDTO.builder()
                .id(estudiante.getId())
                .ingreso(estudiante.getIngreso())
                .representante(RepresentanteDTO.builder()
                        .autorizado(estudiante.getRepresentante().isAutorizado())
                        .ocupacion(estudiante.getRepresentante().getOcupacion())
                        .empresa(estudiante.getRepresentante().getEmpresa())
                        .direccion(estudiante.getRepresentante().getDireccion())
                        .telefono(estudiante.getRepresentante().getTelefono())
                        .build()
                )
                .build() : null;

        RepresentanteDTO representanteDTO = representante != null ? RepresentanteDTO.builder()
                .id(representante.getId())
                .autorizado(representante.isAutorizado())
                .ocupacion(representante.getOcupacion())
                .empresa(representante.getEmpresa())
                .direccion(representante.getDireccion())
                .telefono(representante.getTelefono())
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
                .representante(representanteDTO)
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
