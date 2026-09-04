package com.uade.demo.model;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "descuentos")
public class Descuento {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(nullable = false)
    private double porcentaje;

    @Column(nullable = false)
    private boolean activo = true;

    @Column(name = "aplica_a_todas", nullable = false)
    private boolean aplicaATodas = false;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "descuento_pelicula",
        joinColumns = @JoinColumn(name = "descuento_id"),
        inverseJoinColumns = @JoinColumn(name = "pelicula_id"))
    private List<Peliculas> peliculas = new ArrayList<>();
}
