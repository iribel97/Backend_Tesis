package com.tesis.BackV2.dto;

import com.tesis.BackV2.entities.Docente;
import com.tesis.BackV2.entities.Estudiante;
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
public class UsuarioDTO {
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

    private Docente docente;
    private Estudiante estudiante;

}
