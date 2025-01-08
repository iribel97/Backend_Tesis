package com.tesis.BackV2.entities;

import com.tesis.BackV2.enums.TipoConducta;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Builder
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Conducta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Enumerated(EnumType.STRING)
    private TipoConducta tipo;

    @ManyToOne(fetch = FetchType.LAZY)
    private SistemaCalificacion sistemaCalificacion;
    @ManyToOne(fetch = FetchType.LAZY)
    private Estudiante estudiante;
    @ManyToOne(fetch = FetchType.LAZY)
    private Distributivo distributivo;
    @ManyToOne(fetch = FetchType.LAZY)
    private CicloAcademico cicloAcademico;
}
