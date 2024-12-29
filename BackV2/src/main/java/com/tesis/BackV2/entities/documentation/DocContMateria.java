package com.tesis.BackV2.entities.documentation;

import com.tesis.BackV2.entities.contenido.Asignacion;
import com.tesis.BackV2.entities.contenido.Tema;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class DocContMateria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Lob
    @Basic(fetch = FetchType.LAZY)
    private byte[] contenido;
    private String nombre;
    private String mime;

    @ManyToOne
    private Asignacion asignacion;
}
