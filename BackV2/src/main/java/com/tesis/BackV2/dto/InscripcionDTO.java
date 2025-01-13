package com.tesis.BackV2.dto;

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
public class InscripcionDTO {
    private String cedula;
    private String nombres;
    private String apellidos;
    private String email;
    private String grado;
    private String telefono;
    private String direccion;
    private String fechaNacimiento;
    private String fechaIns;
    private String genero;
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
    private String estado;
    private String fechaInscripcion;
    private List<DocumentoDTO> documentos;
}
