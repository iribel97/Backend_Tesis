package com.tesis.BackV2.repositories.contenido;

import com.tesis.BackV2.entities.contenido.Tema;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TemaRepo extends JpaRepository<Tema, Long> {

    // listar temas por unidad
    List<Tema> findByUnidadId(long idUnidad);

    // listar temas por unidad y activo
    List<Tema> findByUnidadIdAndActivo(long idUnidad, boolean activo);

    // traer un tema activo
    Tema findByIdAndActivo(long id, boolean activo);
}
