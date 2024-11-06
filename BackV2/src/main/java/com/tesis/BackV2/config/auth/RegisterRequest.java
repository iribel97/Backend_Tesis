package com.tesis.BackV2.config.auth;

import com.tesis.BackV2.enums.EstadoUsu;
import com.tesis.BackV2.enums.Genero;
import com.tesis.BackV2.enums.Rol;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {

    /*------ ATRIBUTOS DEL USUARIO GENERAL -------*/
    private String cedula;
    private String nombre;
    private String apellido;
    private String correo;
    private String password;
    private String telefono;
    private String direccion;
    private Rol rol;
    private LocalDate nacimiento;
    private Genero genero;
    private EstadoUsu estado;

    private MultipartFile foto; // Foto de perfil del usuario

    /*------ ATRIBUTOS DEL DOCENTE -------*/
    private String titulo;
    private String especialidad;
    private int experiencia;

    /*------ ATRIBUTOS DEL ESTUDIANTE -------*/
    private LocalDate ingreso;
    private String sangre;
    private String cedulaRepresentante;

    /*------ ATRIBUTOS DEL REPRESENTANTE -------*/
    private boolean autorizado;
    private String ocupacion;
    private String empresa;
    private String direccionEmpresa;
    private String telefonoEmpresa;

}
