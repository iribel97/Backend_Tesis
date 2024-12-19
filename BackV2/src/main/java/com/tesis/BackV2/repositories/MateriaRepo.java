package com.tesis.BackV2.repositories;

import com.tesis.BackV2.entities.Materia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MateriaRepo extends JpaRepository<Materia, Long> {

    // Existe por nombre
    boolean existsByNombre(String nombre);

    // Existe por grado
    boolean existsByGradoNombre(String grado);

    // Traer por nombre de la materia
    List<Materia> findByNombre(String nombre);

    // Traer por nombre del grado
    List<Materia> findByGradoNombre(String grado);

    // Traer por area
    List<Materia> findByArea(String area);

    // Traer por nombre de la materia y grado y area
    Materia findByNombreAndGradoNombre(String nombre, String grado);

    // Existe por id del grado
    boolean existsByGradoId(Long id);

    boolean existsByNombreAndGradoNombre(String nombre, String grado);
}
