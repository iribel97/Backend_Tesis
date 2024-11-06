package com.tesis.BackV2.repositories;

import com.tesis.BackV2.entities.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UsuarioRepo extends JpaRepository<Usuario, String> {

    // Regrese un usuario por cedula
    Usuario findByCedula(String cedula);

    //Comprobar si el usuario existe
    boolean existsByCedula(String cedula);
}
