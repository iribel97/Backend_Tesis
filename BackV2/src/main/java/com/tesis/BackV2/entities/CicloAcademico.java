package com.tesis.BackV2.entities;

import com.tesis.BackV2.entities.config.InscripcionConfig;
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
public class CicloAcademico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String nombre;
    private int cantPeriodos;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private boolean activo;

    @OneToOne(fetch = FetchType.LAZY)
    private InscripcionConfig inscripConfig;
}
