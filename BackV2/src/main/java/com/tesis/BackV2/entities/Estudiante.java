package com.tesis.BackV2.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Estudiante {

    /*----- ATRIBUTOS BASICOS -----*/
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private LocalDate ingreso;  //Fecha de ingreso del estudiante a la institucion
    private String sangre;  //Tipo de sangre del estudiante

    /*----- ATRIBUTOS RELACIONADOS -----*/
    @OneToOne
    private Usuario usuario;

    @ManyToOne
    private Usuario representante;
}
