package com.tesis.BackV2.repositories.documentation;

import com.tesis.BackV2.entities.documentation.DocContMateria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DocMaterialApoyoRepo extends JpaRepository<DocContMateria, Long> {

    // Listar por asignación
    List<DocContMateria> findByAsignacion_Id(Long idAsignacion);

    void deleteByAsignacion_Id(long idAsignacion);
}
