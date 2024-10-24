package com.tesis.BackV2.repositories;

import com.tesis.BackV2.entities.Docente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DocenteRepo extends JpaRepository<Docente, Long> {

    // Función que retorne un docente por medio de la cedula del usuario
    Docente findByUsuarioCedula(String cedula);
}
