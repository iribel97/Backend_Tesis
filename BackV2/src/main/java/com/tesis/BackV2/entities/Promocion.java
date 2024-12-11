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
public class Promocion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String nombre;

    /* ----------------- ATRIBUTOS RELACIONADOS ----------------- */
    @OneToOne(fetch = FetchType.LAZY)
    private CicloAcademico cicloAcademico;
    @ManyToOne(fetch = FetchType.LAZY)
    private Grado grado;
}
