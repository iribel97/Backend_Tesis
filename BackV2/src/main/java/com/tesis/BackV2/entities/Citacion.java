package com.tesis.BackV2.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Builder
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Citacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate fecha; // Fecha de la citación
    private LocalTime hora; // Hora de la citación
    private String motivo; // Razón o motivo de la citación
    private String observaciones; // Observaciones adicionales del docente
    private boolean confirmada; // Indica si el representante ha confirmado su asistencia

    /* ---- ATRIBUTOS RELACIONADOS ---- */
    @ManyToOne(fetch = FetchType.LAZY)
    private Docente docente; // Docente que realiza la citación

    @ManyToOne(fetch = FetchType.LAZY)
    private Estudiante estudiante; // Estudiante relacionado a la citación

    @ManyToOne(fetch = FetchType.LAZY)
    private Representante representante; // Representante del estudiante citado

    @ManyToOne(fetch = FetchType.LAZY)
    private CicloAcademico cicloAcademico; // Ciclo académico al que pertenece la citación


}
