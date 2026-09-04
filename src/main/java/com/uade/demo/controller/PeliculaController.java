package com.uade.demo.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.uade.demo.exception.ResourceNotFoundException;
import com.uade.demo.model.Peliculas;
import com.uade.demo.service.PeliculaService;

@RestController
@RequestMapping("/api/peliculas")
public class PeliculaController {

    private final PeliculaService peliculaService;

    public PeliculaController(PeliculaService peliculaService) {
        this.peliculaService = peliculaService;
    }

    // devolver todas las peliculas
    // get localhost:8080/api/peliculas
    @GetMapping
    public List<Peliculas> getAllPeliculas() {
        return peliculaService.findAll();
    }

    // buscar por id
    // get localhost:8080/api/peliculas/1
    @GetMapping("/{id}")
    public ResponseEntity<Peliculas> getPeliculaById(@PathVariable Long id) {
        Peliculas pelicula = peliculaService.findById(id);
        if (pelicula == null) {
            throw new ResourceNotFoundException("No se encontró la película con id: " + id);
        }
        return ResponseEntity.ok(pelicula);
    }

    // crear pelicula
    // post localhost:8080/api/peliculas
    @PostMapping
    public ResponseEntity<Peliculas> createPelicula(@RequestBody Peliculas pelicula) {
        if (pelicula.getTitulo() == null || pelicula.getTitulo().isEmpty()) {
            throw new IllegalArgumentException("El título de la película es requerido");
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(peliculaService.save(pelicula));
    }

    // actualizar pelicula por id
    // put localhost:8080/api/peliculas/1
    @PutMapping("/{id}")
    public Peliculas updatePelicula(@PathVariable Long id, @RequestBody Peliculas pelicula) {
        Peliculas existente = peliculaService.findById(id);
        if (existente == null) {
            throw new ResourceNotFoundException("No se encontró la película con id: " + id);
        }
        existente.setTitulo(pelicula.getTitulo());
        existente.setDuracion(pelicula.getDuracion());
        existente.setClasificacion(pelicula.getClasificacion());
        existente.setSinopsis(pelicula.getSinopsis());
        existente.setPosterUrl(pelicula.getPosterUrl());
        return peliculaService.save(existente);
    }

    // borrar pelicula por id
    // delete localhost:8080/api/peliculas/1
    @DeleteMapping("/{id}")
    public void deletePelicula(@PathVariable Long id) {
        peliculaService.deleteById(id);
    }

    // eliminar todas las peliculas
    // delete localhost:8080/api/peliculas
    @DeleteMapping
    public void deleteAllPeliculas() {
        peliculaService.deleteAll();
    }
}