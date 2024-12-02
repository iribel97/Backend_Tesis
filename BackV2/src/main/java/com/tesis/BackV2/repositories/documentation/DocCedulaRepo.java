package com.tesis.BackV2.repositories.documentation;

import com.tesis.BackV2.entities.documentation.DocCedula;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DocCedulaRepo extends JpaRepository<DocCedula, Long> {
}
