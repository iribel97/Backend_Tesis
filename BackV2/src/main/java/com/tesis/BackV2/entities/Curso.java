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
public class Curso {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String paralelo;
    private int maxEstudiantes;
    private int estudiantesAsignados;


    /*---- ATRIBUTOS RELACIONADOS ----*/
    @ManyToOne
    private Grado grado;
    @OneToOne
    private Docente tutor;
}
