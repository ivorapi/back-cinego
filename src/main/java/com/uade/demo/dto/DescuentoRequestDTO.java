package com.uade.demo.dto;

import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class DescuentoRequestDTO {
    private String nombre;
    private double porcentaje;
    private boolean aplicaATodas;
    private List<Long> peliculaIds;
}
