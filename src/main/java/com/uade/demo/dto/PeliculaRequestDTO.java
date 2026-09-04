package com.uade.demo.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PeliculaRequestDTO {

    private String titulo;
    private int duracion;
    private double clasificacion;
    private String sinopsis;
    private String posterUrl;
}