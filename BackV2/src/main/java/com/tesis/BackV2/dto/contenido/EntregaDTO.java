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
public class EntregaDTO {

    private long id;
    private String contenido;
    private String nota;
    private String estado;
    private String fechaEntrega;
    private String horaEntrega;

    private String nombresEstudiante;

    private List<DocumentoDTO> documentos;
}
