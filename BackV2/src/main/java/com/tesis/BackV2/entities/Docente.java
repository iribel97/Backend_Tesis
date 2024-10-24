package com.tesis.BackV2.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Docente {

    /*----- ATRIBUTOS BASICOS -----*/
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    /*----- ATRIBUTOS PROFESIONALES -----*/
    private String titulo;        //Titulo del docente  (ej. LICENCIADO, MAESTRÍA).
    private String especialidad;  //Especialidad del docente (ej. MATEMÁTICAS, CIENCIAS).
    private int experiencia;             //Años de experiencia del docente

    /*----- ATRIBUTOS RELACIONADOS -----*/
    @OneToOne
    private Usuario usuario;
}
