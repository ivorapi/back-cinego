package com.uade.demo.dto;

import lombok.Data;

@Data
public class CrearAdminRequestDTO {
    private String email;
    private String nombre;
    private String apellido;
    private String password;
}