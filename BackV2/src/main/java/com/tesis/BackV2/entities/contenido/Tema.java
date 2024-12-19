package com.tesis.BackV2.entities.contenido;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Builder
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Tema {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private boolean activo;
    private String tema;
    private String detalle;

    /* ---- ATRIBUTOS RELACIONADOS ---- */
    @ManyToOne
    private Unidad unidad;
}
