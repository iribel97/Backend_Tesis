package com.tesis.BackV2.entities;

import com.tesis.BackV2.enums.DiaSemana;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalTime;

@Entity
@Getter
@Setter
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Horario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Enumerated(EnumType.STRING)
    private DiaSemana diaSemana;
    private LocalTime horaInicio;
    private LocalTime horaFin;
    private int cantHoras;

    /* ---------- ATRIBUTOS RELACIONADOS ----------*/
    @ManyToOne
    private Distributivo distributivo;
}
