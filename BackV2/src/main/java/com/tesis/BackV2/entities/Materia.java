package com.tesis.BackV2.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Materia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String nombre;
    private String area;
    private int horas;
    private int registroCalificacion;

    /*---- ATRIBUTOS RELACIONADOS ----*/
    @ManyToOne
    private Grado grado;
}
