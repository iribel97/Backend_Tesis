package com.tesis.BackV2.repositories;

import com.tesis.BackV2.entities.Inscripcion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InscripcionRepo extends JpaRepository<Inscripcion, String> {
    // Traer por la cedula del representante
    List<Inscripcion> findByRepresentante_Usuario_Cedula (String cedula);

    // Traes por estado Pendiente
    @Query("SELECT i FROM Inscripcion i WHERE i.estado = 'PENDIENTE'")
    List<Inscripcion> findByEstadoPendiente();
}
