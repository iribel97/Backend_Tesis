package com.tesis.BackV2.entities.documentation;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class DocCertifNota {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Lob @Basic(fetch = FetchType.LAZY)
    private byte[] contenido;
    private String nombre;
    private String mime;

}
