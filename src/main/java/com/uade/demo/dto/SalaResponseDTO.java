package com.uade.demo.dto;

import com.uade.demo.model.Salas;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class SalaResponseDTO {

    private Long id;
    private String nombre;
    private int filas;
    private int asientosPorFila;
    private int cantidadAsientos;

    public SalaResponseDTO(Salas sala) {
        this.id = sala.getId();
        this.nombre = sala.getNombre();
        this.filas = sala.getFilas();
        this.asientosPorFila = sala.getAsientosPorFila();
        this.cantidadAsientos = sala.getFilas() * sala.getAsientosPorFila();
    }
}
