package com.tesis.BackV2.request;

import com.tesis.BackV2.enums.EstadoInscripcion;
import com.tesis.BackV2.enums.Genero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class InscripcionRequest {
    /* ----------------- DATOS DEL ESTUDIANTE -----------------*/
    private String cedula;
    private String nombres;
    private String apellidos;
    private String email;
    private String telefono;
    private String direccion;
    private LocalDate fechaNacimiento;
    private Genero genero;

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

    /* ----------------- REPRESENTANTE ----------------- */
    private String representanteId;
}
