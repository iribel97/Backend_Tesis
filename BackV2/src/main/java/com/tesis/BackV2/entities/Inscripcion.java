package com.tesis.BackV2.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Inscripcion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    /* ----------------- DATOS DEL ESTUDIANTE -----------------*/
    private String nombres;
    private String apellidos;
    private String cedula;
    private String email;
    private String telefono;
    private String direccion;

    // Documentos de identidad

    
}
