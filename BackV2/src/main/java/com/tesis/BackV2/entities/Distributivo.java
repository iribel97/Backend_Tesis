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
public class Distributivo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private int horasAsignadas;

    /*---- ATRIBUTOS RELACIONADOS ----*/
    @ManyToOne
    private CicloAcademico ciclo;
    @ManyToOne
    private Aula aula;
    @ManyToOne
    private Materia materia;
    @ManyToOne
    private Docente docente;
}
