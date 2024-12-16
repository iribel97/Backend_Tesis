package com.tesis.BackV2.repositories.documentation;

import com.tesis.BackV2.entities.documentation.DocEntrega;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DocEntregaRepo extends JpaRepository<DocEntrega, Long> {
}
