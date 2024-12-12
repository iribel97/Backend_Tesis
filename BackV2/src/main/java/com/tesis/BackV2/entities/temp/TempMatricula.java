package com.tesis.BackV2.entities.temp;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;

@Entity
@Builder
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class TempMatricula {

    @Id
    private String cedula;
    private String grado;
    private String paralelo;

}
