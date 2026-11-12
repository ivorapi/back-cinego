package com.uade.demo.dto;

import java.util.List;

import com.uade.demo.model.Asiento;
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
    private int filas;
    private int asientosPorFila;

    public SalaResponseDTO(Salas sala) {
        List<Asiento> asientos = sala.getAsientos() != null ? sala.getAsientos() : List.of();

        this.id = sala.getId();
        this.nombre = sala.getNombre();
        this.cantidadAsientos = asientos.size();
        // Se calculan de los asientos: cuántas filas distintas hay y el número más alto
        this.filas = (int) asientos.stream().map(Asiento::getFila).distinct().count();
        this.asientosPorFila = asientos.stream().mapToInt(Asiento::getNumero).max().orElse(0);
    }
}
