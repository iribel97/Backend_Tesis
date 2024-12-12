package com.tesis.BackV2.enums;

import lombok.Getter;

@Getter
public enum TipoPrueba {

    CONOCIMIENTO("Prueba de conocimiento", "Evaluar los conocimientos del estudiante en áreas clave (matemáticas, lengua, ciencias)."),
    PSICOLOGICA("Prueba Psicológica", "Evaluar aspectos emocionales, sociales, y habilidades cognitivas del estudiante."),
    ENTREVISTA("Entrevista Personal", "Conocer al estudiante y su familia para evaluar aspectos no académicos.");

    private final String tipo;
    private final String descripcion;

    TipoPrueba(String tipo, String descripcion) {
        this.tipo = tipo;
        this.descripcion = descripcion;
    }

}
