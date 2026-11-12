package com.uade.demo.dto;

import com.uade.demo.model.Rol;
import com.uade.demo.model.Usuarios;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// datos públicos del usuario (sin la contraseña)
@Getter
@Setter
@NoArgsConstructor
public class UsuarioResponseDTO {

    private Long id;
    private String email;
    private String nombre;
    private String apellido;
    private Rol rol;

    public UsuarioResponseDTO(Usuarios usuario) {
        this.id = usuario.getId();
        this.email = usuario.getEmail();
        this.nombre = usuario.getNombre();
        this.apellido = usuario.getApellido();
        this.rol = usuario.getRol();
    }
}
