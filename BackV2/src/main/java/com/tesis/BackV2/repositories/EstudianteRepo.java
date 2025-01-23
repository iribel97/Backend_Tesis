package com.tesis.BackV2.repositories;

import com.tesis.BackV2.entities.Estudiante;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EstudianteRepo extends JpaRepository<Estudiante, Long> {

    // Función que retorne un estudiante por medio de la cedula del usuario
    Estudiante findByUsuarioCedula(String cedula);


    List<Estudiante> findByRepresentanteId(long id);

    List<Estudiante> findByRepresentante_Id(long representanteId);
}
