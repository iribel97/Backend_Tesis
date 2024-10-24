package com.tesis.BackV2.clases;

import com.tesis.BackV2.entities.Docente;
import com.tesis.BackV2.entities.Usuario;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegistroDocenteDTO {
    private Usuario usuario;
    private Docente docente;
}
