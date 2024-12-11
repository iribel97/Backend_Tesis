package com.tesis.BackV2.entities.documentation;

import com.tesis.BackV2.entities.Inscripcion;
import com.tesis.BackV2.enums.EstadoInscripcion;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InscripPruebaAdicional {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String tipoPrueba;
    private String descripcion;
    @Enumerated(EnumType.STRING)
    private EstadoInscripcion resultado; // Ejemplo: "Aprobado", "Reprobado"
    private LocalDate fechaPrueba;

    @ManyToOne(fetch = FetchType.LAZY)
    private Inscripcion inscripcion;
}
