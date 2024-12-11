package com.tesis.BackV2.entities;

import com.tesis.BackV2.enums.EstadoMatricula;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Matricula {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private LocalDate fechaMatricula;
    @Enumerated(EnumType.STRING)
    private EstadoMatricula estado;

    /* ---- ATRIBUTOS RELACIONADOS ---- */
    @ManyToOne(fetch = FetchType.LAZY)
    private Inscripcion inscripcion;
    @ManyToOne(fetch = FetchType.LAZY)
    private Curso curso;
    @ManyToOne(fetch = FetchType.LAZY)
    private Grado grado;
}
