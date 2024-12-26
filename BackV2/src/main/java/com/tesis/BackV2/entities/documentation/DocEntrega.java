package com.tesis.BackV2.entities.documentation;

import com.tesis.BackV2.entities.Estudiante;
import com.tesis.BackV2.entities.contenido.Entrega;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class DocEntrega {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Lob
    @Basic(fetch = FetchType.LAZY)
    private byte[] contenido;
    private String nombre;
    private String mime;

    /* ---- ATRIBUTOS RELACIONADOS ----- */
    @ManyToOne
    private Estudiante estudiante;
    @ManyToOne
    private Entrega entrega;
}
