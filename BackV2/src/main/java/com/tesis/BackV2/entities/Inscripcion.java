package com.tesis.BackV2.entities;

import com.tesis.BackV2.entities.documentation.DocCedula;
import com.tesis.BackV2.entities.documentation.DocCertifNota;
import com.tesis.BackV2.entities.documentation.DocServBasicos;
import com.tesis.BackV2.enums.EstadoInscripcion;
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
public class Inscripcion {

    @Id
    @Column(length = 10)
    private String cedula;

    /* ----------------- DATOS DEL ESTUDIANTE -----------------*/
    private String nombres;
    private String apellidos;
    private String email;
    private String telefono;
    private String direccion;
    private LocalDate fechaNacimiento;

    /* ----------------- DATOS FAMILIARES ----------------- */
    private String nombresPadre;
    private String apellidosPadre;
    private String correoPadre;
    private String telefonoPadre;
    private String ocupacionPadre;

    private String nombresMadre;
    private String apellidosMadre;
    private String correoMadre;
    private String telefonoMadre;
    private String ocupacionMadre;

    /* ----------------- CONTROL DE INSCRIPCION ----------------- */
    @Enumerated(EnumType.STRING)
    private EstadoInscripcion estado;
    private LocalDate fechaInscripcion;

    /* ----------------- ATRIBUTOS RELACIONADOS ----------------- */
    @OneToOne(fetch = FetchType.LAZY)
    private DocCedula cedulaEstudiante;

    @OneToOne(fetch = FetchType.LAZY)
    private DocCedula cedulaPadre;

    @OneToOne(fetch = FetchType.LAZY)
    private DocCedula cedulaMadre;
    
    @OneToOne(fetch = FetchType.LAZY)
    private DocCertifNota certificadoNotas;

    @OneToOne(fetch = FetchType.LAZY)
    private DocServBasicos serviciosBasicos;

    @OneToOne(fetch = FetchType.LAZY)
    private Representante representante;
    
}
