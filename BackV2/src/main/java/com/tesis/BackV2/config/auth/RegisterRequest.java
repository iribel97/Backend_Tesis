package com.tesis.BackV2.config.auth;

import com.tesis.BackV2.enums.Genero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {

    /*------ ATRIBUTOS DEL USUARIO GENERAL -------*/
    private String cedula;
    private String nombres;
    private String apellidos;
    private String correo;
    private String password;
    private String telefono;
    private String direccion;
    private LocalDate nacimiento;
    private Genero genero;

    /*------ ATRIBUTOS DEL DOCENTE -------*/
    private String titulo;
    private String especialidad;
    private int experiencia;

    /*------ ATRIBUTOS DEL ESTUDIANTE -------*/
    private LocalDate ingreso;
    private String sangre;
    private String cedulaRepresentante;

    /*------ ATRIBUTOS DEL REPRESENTANTE -------*/
    private String ocupacion;
    private String empresa;
    private String direccionEmpresa;
    private String telefonoEmpresa;

}
