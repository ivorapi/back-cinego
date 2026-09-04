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
@Table(name = "salas")
public class Salas {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nombre;

    @JsonIgnore
    @OneToMany(mappedBy = "sala")
    private List<Asiento> asientos = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "sala")
    private List<Funcion> funciones = new ArrayList<>();
}
