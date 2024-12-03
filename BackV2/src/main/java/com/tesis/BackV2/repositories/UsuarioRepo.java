package com.tesis.BackV2.repositories;

import com.tesis.BackV2.entities.Usuario;
import com.tesis.BackV2.enums.Rol;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UsuarioRepo extends JpaRepository<Usuario, String> {

    // Regrese un usuario por cedula
    Usuario findByCedula(String cedula);

    //Comprobar si el usuario existe
    boolean existsByCedula(String cedula);

    // Traer usuarios por rol
    List<Usuario> findByRol(Rol rol);

    // Traer usuarios de un mismo rol pero no con un numero de cedula especifico
    List<Usuario> findByRolAndCedulaNot(Rol rol, String cedula);
}
