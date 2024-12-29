package com.tesis.BackV2.dto.contenido;

import com.tesis.BackV2.dto.doc.DocumentoDTO;
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
    private boolean activo;
    private String nombre;
    private String descripcion;
    private String fechaFin;
    private String horaFin;
    private List<DocumentoDTO> documentos;
}
