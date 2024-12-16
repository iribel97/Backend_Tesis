package com.tesis.BackV2.entities.contenido;

import com.tesis.BackV2.entities.Estudiante;
import jakarta.persistence.*;
import lombok.*;

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

    /* ----- ATRIBUTOS RELACIONADOS ---- */
    @ManyToOne
    private Asignacion asignacion;
    @ManyToOne
    private Estudiante estudiante;

}
