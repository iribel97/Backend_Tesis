package com.tesis.BackV2.repositories.temp;

import com.tesis.BackV2.entities.temp.TempMatricula;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TempMatriculaRpo extends JpaRepository<TempMatricula, String> {
    TempMatricula findByCedula(String cedula);
}
