package com.tesis.BackV2.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Representante {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private boolean autorizado; // AUTORIZADO PARA RETIRAR

    /* ----------- ATRIBUTOS LABORALES ------------- */
    private String ocupacion;
    private String empresa;    // NOMBRE DE LA EMPRESA
    private String direccion;  // DIRECCION DE LA EMPRESA
    private String telefono;   // TELEFONO DE LA EMPRESA

    @OneToOne
    private Usuario usuario;
}
