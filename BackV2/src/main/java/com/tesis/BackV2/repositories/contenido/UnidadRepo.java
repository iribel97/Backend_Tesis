package com.tesis.BackV2.repositories.contenido;

import com.tesis.BackV2.entities.contenido.Unidad;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UnidadRepo extends JpaRepository<Unidad, Long> {

    // Traer unidades por distributivo
    public List<Unidad> findByDistributivo_Id(long idDistributivo);

    // Traer unidades por distributivo y activo
    public List<Unidad> findByDistributivo_IdAndActivo(long idDistributivo, boolean activo);

    List<Unidad> findByDistributivoId(Long id);
}
