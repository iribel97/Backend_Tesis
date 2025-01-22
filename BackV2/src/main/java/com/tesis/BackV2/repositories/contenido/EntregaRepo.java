package com.tesis.BackV2.repositories.contenido;

import com.tesis.BackV2.entities.contenido.Entrega;
import com.tesis.BackV2.enums.EstadoEntrega;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EntregaRepo extends JpaRepository<Entrega, Long> {

    // Listar por asignación
    List<Entrega> findByAsignacion_Id(Long idAsignacion);

    //Listar por seignación y estudiante
    Entrega findByAsignacion_IdAndEstudiante_Id(Long idAsignacion, long idEstudiante);

    // Listar por estudiante y distributivo id
    List<Entrega> findByEstudiante_IdAndAsignacion_Tema_Unidad_Distributivo_Id(Long idEstudiante, Long idDistributivo);

    // Listar por distributivo id
    List<Entrega> findByAsignacion_Tema_Unidad_Distributivo_Id(Long idDistributivo);

    List<Entrega> findByEstudiante_IdAndActivoAndEstado(long estudianteId, boolean activo, EstadoEntrega estado);
}
