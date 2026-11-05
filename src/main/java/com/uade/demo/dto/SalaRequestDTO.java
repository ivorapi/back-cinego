package com.uade.demo.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class SalaRequestDTO {

    private String nombre;

    // Distribución de la sala: filas (A, B, C...) y asientos por fila.
    // Al editar se pueden mandar en null para dejar los asientos como están.
    private Integer filas;
    private Integer asientosPorFila;
}
