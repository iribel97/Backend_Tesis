package com.tesis.BackV2.entities;

import com.tesis.BackV2.entities.embedded.Calificacion;
import com.tesis.BackV2.enums.TipoSistCalif;
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
public class SistemaCalificacion {

    @EmbeddedId
    private Calificacion id;

    private String descripcion;
    private String peso;
    @Enumerated(EnumType.STRING)
    private TipoSistCalif tipo;
    private String base;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;

    /* ------ ATRIBUTOS RELACIONADOS ----- */
    @ManyToOne
    private CicloAcademico ciclo;
}
