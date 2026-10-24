package com.uade.demo.dto;

import java.math.BigDecimal;

import com.uade.demo.model.Candy;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CandyResponseDTO {
    private Long id;
    private String nombre;
    private String descripcion;
    private BigDecimal precio;
    private String imagenUrl;

    public CandyResponseDTO(Candy candy) {
        this.id = candy.getId();
        this.nombre = candy.getNombre();
        this.descripcion = candy.getDescripcion();
        this.precio = candy.getPrecio();
        this.imagenUrl = candy.getImagenUrl();
    }
}
