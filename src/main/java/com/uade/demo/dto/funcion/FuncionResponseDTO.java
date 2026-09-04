package com.uade.demo.dto.funcion;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// DTO de salida: lo que la API devuelve al cliente. Incluye el id de la
// funcion y, además de los ids de pelicula/sala, sus nombres para que el
// cliente no tenga que pedirlos aparte. Evita exponer las entidades Funcion,
// Peliculas y Salas directamente y previene problemas de serialización circular.
@Getter
@Setter
@NoArgsConstructor
public class FuncionResponseDTO {
    private Long id;

    private Long peliculaId;
    private String peliculaTitulo;

    private Long salaId;
    private String salaNombre;

    private LocalDateTime horarioInicio;
    private BigDecimal precio;

    public FuncionResponseDTO(Long id, Long peliculaId, String peliculaTitulo, Long salaId, String salaNombre,
            LocalDateTime horarioInicio, BigDecimal precio) {
        this.id = id;
        this.peliculaId = peliculaId;
        this.peliculaTitulo = peliculaTitulo;
        this.salaId = salaId;
        this.salaNombre = salaNombre;
        this.horarioInicio = horarioInicio;
        this.precio = precio;
    }
}
