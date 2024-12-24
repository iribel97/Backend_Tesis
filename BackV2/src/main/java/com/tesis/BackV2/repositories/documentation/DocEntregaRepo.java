package com.tesis.BackV2.repositories.documentation;

import com.tesis.BackV2.entities.contenido.Entrega;
import com.tesis.BackV2.entities.documentation.DocEntrega;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DocEntregaRepo extends JpaRepository<DocEntrega, Long> {

    // Listar por entrega
    List<DocEntrega> findByEntrega(Entrega entrega);
}
