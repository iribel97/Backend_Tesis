package com.tesis.BackV2.entities.contenido;

import com.tesis.BackV2.entities.Estudiante;
import com.tesis.BackV2.enums.EstadoEntrega;
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
public class Entrega {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private boolean activo;
    private String contenido;
    private String nota;
    @Enumerated(EnumType.STRING)
    private EstadoEntrega estado;
    private LocalDate fechaEntrega;
    private LocalTime horaEntrega;

    /* ----- ATRIBUTOS RELACIONADOS ---- */
    @ManyToOne
    private Asignacion asignacion;
    @ManyToOne
    private Estudiante estudiante;

}
