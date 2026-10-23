package com.uade.demo.dto;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CandyRequestDTO {
    private String nombre;
    private String descripcion;
    private BigDecimal precio;
    private String imagenUrl;
}
