package com.tesis.BackV2.request;

import com.tesis.BackV2.enums.EstadoUsu;
import com.tesis.BackV2.enums.Genero;
import com.tesis.BackV2.enums.Rol;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UsuarioRequest {
    /*------ ATRIBUTOS DEL USUARIO GENERAL -------*/
    private String cedula;
    private String nombres;
    private String apellidos;
    private String correo;
    private String telefono;
    private String direccion;
    private LocalDate nacimiento;
    private Genero genero;
    private Rol rol;
    private EstadoUsu estado;

    private Long id;
    /*------ ATRIBUTOS DEL DOCENTE -------*/
    private String titulo;
    private String especialidad;
    private int experiencia;

    /*------ ATRIBUTOS DEL ESTUDIANTE -------*/
    private LocalDate ingreso;
    private String cedulaRepresentante;
}

