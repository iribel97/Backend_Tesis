package com.tesis.BackV2.repositories.contenido;

import com.tesis.BackV2.entities.contenido.Asignacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AsignacionRepo extends JpaRepository<Asignacion, Long> {

    // traer por tema
    List<Asignacion> findByTema_Id(Long idTema);

    // traer por tema y activo
    List<Asignacion> findByTema_IdAndActivo(Long idTema, boolean activo);
}
