package com.uade.demo.model;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "peliculas")
public class Peliculas {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String titulo;

    @Column(nullable = false)
    private int duracion;

    @Column(nullable = false)
    private double clasificacion;

    @Column(columnDefinition = "TEXT")
    private String sinopsis;

    @Column(name = "poster_url", length = 255)
    private String posterUrl;

    @JsonIgnore
    @OneToMany(mappedBy = "pelicula")
    private List<Funcion> funciones = new ArrayList<>();
}
