package com.tesis.BackV2.entities;

import com.tesis.BackV2.enums.EstadoAsistencia;
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
public class Asistencia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private LocalDate fecha;

    @Enumerated(EnumType.STRING)
    private EstadoAsistencia estado;
    private String observaciones;

    /* ------ ATRIBUTOS RELACIONADOS ----- */
    @ManyToOne (fetch = FetchType.LAZY)
    private Estudiante estudiante;

    @ManyToOne (fetch = FetchType.LAZY)
    private Horario horario;

    @ManyToOne (fetch = FetchType.LAZY)
    private CicloAcademico cicloAcademico;
}
