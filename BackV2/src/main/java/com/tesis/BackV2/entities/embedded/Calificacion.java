package com.tesis.BackV2.entities.embedded;

import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
@Getter
@Setter
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Calificacion implements Serializable {

    private long registro;
    private long lvl1;
    private long lvl2;
    private long lvl3;
    private long lvl4;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Calificacion that = (Calificacion) o;
        return  Objects.equals(registro, that.registro) &&
                Objects.equals(lvl1, that.lvl1) &&
                Objects.equals(lvl2, that.lvl2) &&
                Objects.equals(lvl3, that.lvl3) &&
                Objects.equals(lvl4, that.lvl4)
                ;
    }

    @Override
    public int hashCode() {
        return Objects.hash( registro, lvl1, lvl2, lvl3, lvl4);
    }
}
