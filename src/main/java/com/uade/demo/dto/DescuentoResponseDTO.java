package com.uade.demo.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DescuentoResponseDTO {
    private Long id;
    private String nombre;
    private double porcentaje;
    private boolean activo;
    private boolean aplicaATodas;
    private List<String> peliculaNombres;
}
