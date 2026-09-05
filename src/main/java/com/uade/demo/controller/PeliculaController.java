package com.uade.demo.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.uade.demo.dto.PeliculaRequestDTO;
import com.uade.demo.dto.PeliculaResponseDTO;
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
    public ResponseEntity<List<PeliculaResponseDTO>> getAllPeliculas() {
        return ResponseEntity.ok(peliculaService.findAll());
    }

    // buscar por id
    // get localhost:8080/api/peliculas/1
    @GetMapping("/{id}")
    public ResponseEntity<PeliculaResponseDTO> getPeliculaById(@PathVariable Long id) {
        return ResponseEntity.ok(peliculaService.findById(id));
    }

    // crear pelicula
    // post localhost:8080/api/peliculas
    @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN')")
    @PostMapping
    public ResponseEntity<PeliculaResponseDTO> createPelicula(@RequestBody PeliculaRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(peliculaService.save(dto));
    }

    // actualizar pelicula por id
    // put localhost:8080/api/peliculas/1
    @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<PeliculaResponseDTO> updatePelicula(@PathVariable Long id,
            @RequestBody PeliculaRequestDTO dto) {
        return ResponseEntity.ok(peliculaService.update(id, dto));
    }

    // borrar pelicula por id
    // delete localhost:8080/api/peliculas/1
    @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePelicula(@PathVariable Long id) {
        peliculaService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    // eliminar todas las peliculas
    // delete localhost:8080/api/peliculas
    @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN')")
    @DeleteMapping
    public ResponseEntity<Void> deleteAllPeliculas() {
        peliculaService.deleteAll();
        return ResponseEntity.noContent().build();
    }
}