package com.tesis.BackV2.services;

import com.tesis.BackV2.entities.Usuario;
import com.tesis.BackV2.repositories.UsuarioRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UsuarioRepo usuarioRepository;

    @Override
    public UserDetails loadUserByUsername(String cedula) throws UsernameNotFoundException {
        // Buscar el usuario por email
        Usuario usuario = usuarioRepository.findByCedula(cedula);
        if (usuario != null) {
            // Crear lista de permisos
            List<GrantedAuthority> permisos = new ArrayList<>();
            GrantedAuthority permiso = new SimpleGrantedAuthority("ROLE_" + usuario.getRol().toString());
            permisos.add(permiso);

            // Devolver un objeto User con email, contraseña y permisos
            return new User(usuario.getEmail(), usuario.getPassword(), permisos);
        } else {
            throw new UsernameNotFoundException("Usuario no encontrado con la cedula: " + cedula);
        }
    }
}
