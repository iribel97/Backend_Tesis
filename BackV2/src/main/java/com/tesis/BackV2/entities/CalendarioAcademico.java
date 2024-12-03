package com.tesis.BackV2.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Builder
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class CalendarioAcademico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String descripcion;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;

    /* ------ ATRIBUTOS RELACIONADOS ----- */
    @ManyToOne (fetch = FetchType.LAZY)
    private CicloAcademico ciclo;
}
