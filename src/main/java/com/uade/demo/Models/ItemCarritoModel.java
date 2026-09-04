package com.uade.demo.Models;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "item_carrito")
@Data
public class ItemCarritoModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "carrito_id", nullable = false)
    private CarritoModel carrito;

    @ManyToOne
    @JoinColumn(name = "producto_id", nullable = false)
    private ProductoModel producto;

    @Column(nullable = false)
    private Integer cantidad;
}