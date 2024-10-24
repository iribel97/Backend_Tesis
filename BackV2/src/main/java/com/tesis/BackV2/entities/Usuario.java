package com.tesis.BackV2.entities;

import com.tesis.BackV2.enums.EstadoUsu;
import com.tesis.BackV2.enums.Genero;
import com.tesis.BackV2.enums.Rol;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Usuario {
    /*----- ATRIBUTOS BASICOS -----*/
    @Id
    @Column(length = 10)
    protected String cedula;

    private String nombres;
    private String apellidos;
    private String email;
    private String password;

    /*----- ATRIBUTOS DE PERFIL -----*/
    private LocalDate nacimiento; //Fecha de nacimiento
    @Enumerated(EnumType.STRING)
    private Genero genero;
    @Enumerated(EnumType.STRING)
    private Rol rol;
    private String direccion;
    private String telefono;

    /*----- ATRIBUTOS DE AUTENTICACION Y SEGURIDAD -----*/
    @Enumerated(EnumType.STRING)
    private EstadoUsu estado;      //Estado del usuario (Activo, Inactivo, Bloqueado)


}
