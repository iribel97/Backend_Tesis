package com.tesis.BackV2.repositories.documentation;

import com.tesis.BackV2.entities.documentation.InscripPruebaAdicional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InscripPruebaAdicionalRepo extends JpaRepository<InscripPruebaAdicional, Long> {

    // Buscar por tipo de prueba y id de inscripción
    InscripPruebaAdicional findByTipoPruebaAndInscripcionCedula(String tipoPrueba, String idInscripcion);
}
