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

import com.uade.demo.dto.SalaRequestDTO;
import com.uade.demo.dto.SalaResponseDTO;
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
    public ResponseEntity<List<SalaResponseDTO>> getAllSalas() {
        return ResponseEntity.ok(salaService.findAll());
    }

    // buscar por id
    // get localhost:8080/api/salas/1
    @GetMapping("/{id}")
    public ResponseEntity<SalaResponseDTO> getSalaById(@PathVariable Long id) {
        return ResponseEntity.ok(salaService.findById(id));
    }

    // crear sala
    // post localhost:8080/api/salas
    @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN')")
    @PostMapping
    public ResponseEntity<SalaResponseDTO> createSala(@RequestBody SalaRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(salaService.save(dto));
    }

    // actualizar sala por id
    // put localhost:8080/api/salas/1
    @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<SalaResponseDTO> updateSala(@PathVariable Long id, @RequestBody SalaRequestDTO dto) {
        return ResponseEntity.ok(salaService.update(id, dto));
    }

    // borrar sala por id
    // delete localhost:8080/api/salas/1
    @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSala(@PathVariable Long id) {
        salaService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    // eliminar todas las salas
    // delete localhost:8080/api/salas
    @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN')")
    @DeleteMapping
    public ResponseEntity<Void> deleteAllSalas() {
        salaService.deleteAll();
        return ResponseEntity.noContent().build();
    }
}