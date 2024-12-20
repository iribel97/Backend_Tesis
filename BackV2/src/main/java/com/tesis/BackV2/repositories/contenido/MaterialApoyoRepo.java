package com.tesis.BackV2.repositories.contenido;

import com.tesis.BackV2.entities.contenido.MaterialApoyo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MaterialApoyoRepo extends JpaRepository<MaterialApoyo, Long> {

    // contar por tema
    long countByTema_Id(long idTema);

    // traer por tema
    List<MaterialApoyo> findByTema_Id(long idTema);

    // traer por tema y que estén activo
    List<MaterialApoyo> findByTema_IdAndActivo(long idTema, boolean activo);
}
