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
    private int cantidadAsientos;

    public SalaResponseDTO(Salas sala) {
        this.id = sala.getId();
        this.nombre = sala.getNombre();
        this.cantidadAsientos = sala.getAsientos() != null ? sala.getAsientos().size() : 0;
    }
}