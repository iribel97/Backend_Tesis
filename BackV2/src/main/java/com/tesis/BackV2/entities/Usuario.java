package com.tesis.BackV2.entities;

import com.tesis.BackV2.enums.EstadoUsu;
import com.tesis.BackV2.enums.Genero;
import com.tesis.BackV2.enums.Rol;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Usuario implements UserDetails {
    /*----- ATRIBUTOS BASICOS -----*/
    @Id
    @Column(length = 10)
    protected String cedula;

    private String nombres;
    private String apellidos;
    private String email;
    private String password;

    /*----- ATRIBUTOS DE PERFIL -----*/
    private LocalDate nacimiento; //Fecha de nacimiento
    @Enumerated(EnumType.STRING)
    private Genero genero;
    @Enumerated(EnumType.STRING)
    private Rol rol;
    private String direccion;
    private String telefono;

    /*----- ATRIBUTOS DE AUTENTICACION Y SEGURIDAD -----*/
    @Enumerated(EnumType.STRING)
    private EstadoUsu estado;      //Estado del usuario (Activo, Inactivo, Bloqueado)
    private LocalDate creacion;     //Fecha de creacion del usuario


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(rol.name()));
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return cedula;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
