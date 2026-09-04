package com.uade.demo.dto;

import com.uade.demo.model.Peliculas;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PeliculaResponseDTO {

    private Long id;
    private String titulo;
    private int duracion;
    private double clasificacion;
    private String sinopsis;
    private String posterUrl;

    public PeliculaResponseDTO(Peliculas pelicula) {
        this.id = pelicula.getId();
        this.titulo = pelicula.getTitulo();
        this.duracion = pelicula.getDuracion();
        this.clasificacion = pelicula.getClasificacion();
        this.sinopsis = pelicula.getSinopsis();
        this.posterUrl = pelicula.getPosterUrl();
    }
}