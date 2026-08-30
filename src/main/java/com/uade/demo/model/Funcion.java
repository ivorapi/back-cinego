package com.uade.demo.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
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
@Table(name = "funciones", uniqueConstraints = @UniqueConstraint(
    name = "uk_funcion_sala_horario", columnNames = {"sala_id", "horario_inicio"}))
public class Funcion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "pelicula_id", nullable = false)
    private Peliculas pelicula;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "sala_id", nullable = false)
    private Salas sala;

    @Column(name = "horario_inicio", nullable = false)
    private LocalDateTime horarioInicio;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal precio;

    @JsonIgnore
    @OneToMany(mappedBy = "funcion")
    private List<Reservas> reservas = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "funcion")
    private List<Entrada> entradas = new ArrayList<>();

    public Long getPeliculaId() {
        return pelicula != null ? pelicula.getId() : null;
    }

    public Long getSalaId() {
        return sala != null ? sala.getId() : null;
    }
}
