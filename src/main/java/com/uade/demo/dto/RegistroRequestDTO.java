package com.uade.demo.dto;

import java.time.LocalDate;
import lombok.Data;

@Data
public class RegistroRequestDTO {
    private String email;
    private String nombre;
    private String apellido;
    private String password;
    private LocalDate fechaNacimiento;
    private String sexo;
}