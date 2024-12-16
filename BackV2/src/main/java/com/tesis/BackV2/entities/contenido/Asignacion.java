package com.tesis.BackV2.entities.contenido;

import com.tesis.BackV2.entities.SistemaCalificacion;
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
public class Asignacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private boolean activo;
    private String nombre;
    private String descripcion;
    private LocalDate fechaInicio;
    private LocalTime horaInicio;
    private LocalDate fechaFin;
    private LocalTime horaFin;

    /* ---- ATRIBUTOS RELACIONADOS ---- */
    @ManyToOne
    private Tema tema;
    @ManyToOne
    private SistemaCalificacion calif;

}
