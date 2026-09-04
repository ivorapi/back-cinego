package com.uade.demo.Models;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "imagen_producto")
@Data
public class ImagenProductoModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String url;

    @ManyToOne
    @JoinColumn(name = "producto_id", nullable = false)
    private ProductoModel producto;
}