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

import com.uade.demo.exception.ResourceNotFoundException;
import com.uade.demo.model.Salas;
import com.uade.demo.service.SalaService;

@RestController
@RequestMapping("/api/salas")
public class SalaController {

    private final SalaService salaService;

    public SalaController(SalaService salaService) {
        this.salaService = salaService;
    }

    // devolver todas las salas
    // get localhost:8080/api/salas
    @GetMapping
    public List<Salas> getAllSalas() {
        return salaService.findAll();
    }

    // buscar por id
    // get localhost:8080/api/salas/1
    @GetMapping("/{id}")
    public ResponseEntity<Salas> getSalaById(@PathVariable Long id) {
        Salas sala = salaService.findById(id);
        if (sala == null) {
            throw new ResourceNotFoundException("No se encontró la sala con id: " + id);
        }
        return ResponseEntity.ok(sala);
    }

    // crear sala
    // post localhost:8080/api/salas
    @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN')")
    @PostMapping
    public ResponseEntity<Salas> createSala(@RequestBody Salas sala) {
        if (sala.getNombre() == null || sala.getNombre().isEmpty()) {
            throw new IllegalArgumentException("El nombre de la sala es requerido");
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(salaService.save(sala));
    }

    // actualizar sala por id
    // put localhost:8080/api/salas/1
    @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN')")
    @PutMapping("/{id}")
    public Salas updateSala(@PathVariable Long id, @RequestBody Salas sala) {
        Salas existente = salaService.findById(id);
        if (existente == null) {
            throw new ResourceNotFoundException("No se encontró la sala con id: " + id);
        }
        existente.setNombre(sala.getNombre());
        return salaService.save(existente);
    }

    // borrar sala por id
    // delete localhost:8080/api/salas/1
    @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN')")
    @DeleteMapping("/{id}")
    public void deleteSala(@PathVariable Long id) {
        salaService.deleteById(id);
    }

    // eliminar todas las salas
    // delete localhost:8080/api/salas
    @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN')")
    @DeleteMapping
    public void deleteAllSalas() {
        salaService.deleteAll();
    }
}