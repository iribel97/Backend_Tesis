package com.tesis.BackV2.entities.contenido;

import com.tesis.BackV2.entities.Distributivo;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Builder
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Unidad {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String titulo;
    private boolean activo;

    /* ----- ATRIBUTOS RELACIONADOS ----- */
    @ManyToOne
    private Distributivo distributivo;
}
