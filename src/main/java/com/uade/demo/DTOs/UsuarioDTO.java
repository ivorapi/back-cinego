package com.uade.demo.DTOs;

import lombok.Data;

@Data
public class UsuarioDTO {
    private Long id;
    private String nombre;
    private String apellido;
    private Double surname;

    public UsuarioDTO(Long id, String nombre, String apellido, Double surname) {
        this.id = id;
        this.nombre = nombre;
        this.apellido = apellido;
        this.surname = surname;
    }
}

