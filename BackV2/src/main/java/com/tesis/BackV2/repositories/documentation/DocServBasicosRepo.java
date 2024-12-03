package com.tesis.BackV2.repositories.documentation;

import com.tesis.BackV2.entities.documentation.DocServBasicos;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DocServBasicosRepo extends JpaRepository<DocServBasicos, Long> {
}
