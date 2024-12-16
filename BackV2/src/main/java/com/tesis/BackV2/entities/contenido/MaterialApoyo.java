package com.tesis.BackV2.entities.contenido;

import com.tesis.BackV2.entities.documentation.Documento;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Builder
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class MaterialApoyo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private boolean activo;
    private String link;
    private String nombreLink;

    /* ------ ATRIBUTOS RELACIONADOS ------ */
    @OneToOne
    private Documento documento;
    @ManyToOne
    private Tema tema;
}
