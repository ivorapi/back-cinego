package com.uade.demo.Models;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "pelicula")
@Data
public class PeliculaModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String titulo;

    @Column(columnDefinition = "TEXT")
    private String descripcion;

    @ManyToOne
    @JoinColumn(name = "categoria_id", nullable = false)
    private CategoriaModel categoria;
}