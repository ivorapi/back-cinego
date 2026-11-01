package com.uade.demo.dto;

import java.math.BigDecimal;

import com.uade.demo.model.Carrito;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CarritoResponseDTO {
    private Long id;
    private Long candyId;
    private String nombre;
    private BigDecimal precio;
    private String imagenUrl;
    private Integer cantidad;

    public CarritoResponseDTO(Carrito carrito) {
        this.id = carrito.getId();
        this.candyId = carrito.getCandyId();
        this.nombre = carrito.getCandy().getNombre();
        this.precio = carrito.getCandy().getPrecio();
        this.imagenUrl = carrito.getCandy().getImagenUrl();
        this.cantidad = carrito.getCantidad();
    }
}
