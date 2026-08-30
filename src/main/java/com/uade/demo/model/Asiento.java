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
@Table(name = "asientos")
public class Asiento {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "sala_id", nullable = false)
    private Salas sala;

    @Column(nullable = false, length = 10)
    private String fila;

    @Column(nullable = false)
    private int numero;

    @JsonIgnore
    @OneToMany(mappedBy = "asiento")
    private List<Entrada> entradas = new ArrayList<>();

    public Long getSalaId() {
        return sala != null ? sala.getId() : null;
    }
}
