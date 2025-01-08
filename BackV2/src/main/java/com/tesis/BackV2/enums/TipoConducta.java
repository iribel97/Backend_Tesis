package com.tesis.BackV2.enums;

import lombok.Getter;

@Getter
public enum TipoConducta {
    A("Muy Satisfactorio", "Lidera el cumplimiento de los compromisos establecidos para la sana convivencia social."),
    B("Satisfactorio", "Cumple con los compromisos establecidos para la sana convivencia social."),
    C("Poco Satisfactorio", "Ocasionalmente incumple los compromisos establecidos para la sana convivencia social."),
    D("Mejorable", "Frecuentemente incumple los compromisos establecidos para la sana convivencia social."),
    E("Insatisfactorio", "Incumple constantemente los compromisos establecidos para la sana convivencia social.");

    private final String descripcion;
    private final String detalle;

    TipoConducta(String descripcion, String detalle) {
        this.descripcion = descripcion;
        this.detalle = detalle;
    }

}
