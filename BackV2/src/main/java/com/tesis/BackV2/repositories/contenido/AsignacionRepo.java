package com.tesis.BackV2.repositories.contenido;

import com.tesis.BackV2.entities.contenido.Asignacion;
import com.tesis.BackV2.enums.EstadoEntrega;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AsignacionRepo extends JpaRepository<Asignacion, Long> {

    // traer por tema
    List<Asignacion> findByTema_Id(Long idTema);

    // traer por tema y activo
    List<Asignacion> findByTema_IdAndActivo(Long idTema, boolean activo);

    @Query("SELECT DISTINCT a FROM Asignacion a " +
            "JOIN a.tema t " +
            "JOIN t.unidad u " +
            "JOIN u.distributivo d " +
            "JOIN d.docente doc " +
            "JOIN Entrega e ON e.asignacion = a " +
            "WHERE doc.id = :idDocente " +
            "AND a.activo = true " +
            "AND (e.nota IS NULL OR e.nota = '' )")
    List<Asignacion> findAsignacionesPendientesPorDocente(@Param("idDocente") Long idDocente);

    // traer asignaciones por id del distributivo
    List<Asignacion> findByTema_Unidad_Distributivo_Id(Long idDistributivo);
}
