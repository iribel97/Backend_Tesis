package com.tesis.BackV2.entities.config;

import com.tesis.BackV2.entities.CicloAcademico;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class InscripcionConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private boolean requierePruebas;

    private LocalDateTime fechaConfiguracion;
}
