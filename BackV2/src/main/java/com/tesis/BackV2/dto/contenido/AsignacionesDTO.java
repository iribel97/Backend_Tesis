package com.tesis.BackV2.dto.contenido;

import com.tesis.BackV2.dto.doc.DocumentoDTO;
import com.tesis.BackV2.entities.embedded.Calificacion;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AsignacionesDTO {

    private long idAsignacion;
    private Calificacion idCalificacion;
    private boolean activo;
    private String nombre;
    private String descripcion;
    private String fechaInicio;
    private String horaInicio;
    private String fechaFin;
    private String horaFin;
    private List<DocumentoDTO> documentos;
}
