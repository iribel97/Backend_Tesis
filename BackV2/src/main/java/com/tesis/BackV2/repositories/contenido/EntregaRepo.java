package com.tesis.BackV2.repositories.contenido;

import com.tesis.BackV2.entities.contenido.Entrega;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EntregaRepo extends JpaRepository<Entrega, Long> {

    // Listar por asignación
    List<Entrega> findByAsignacion_Id(Long idAsignacion);

    //Listar por seignación y estudiante
    List<Entrega> findByAsignacion_IdAndEstudiante_Id(Long idAsignacion, long idEstudiante);
}
