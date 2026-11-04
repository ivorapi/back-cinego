package com.uade.demo.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.uade.demo.dto.CarritoRequestDTO;
import com.uade.demo.dto.CarritoResponseDTO;
import com.uade.demo.service.CarritoService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/carrito")
public class CarritoController {

    private final CarritoService carritoService;

    public CarritoController(CarritoService carritoService) {
        this.carritoService = carritoService;
    }

    @GetMapping
    public ResponseEntity<List<CarritoResponseDTO>> getAll() {
        return ResponseEntity.ok(carritoService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CarritoResponseDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(carritoService.getById(id));
    }

    @PostMapping
    public ResponseEntity<CarritoResponseDTO> add(@Valid @RequestBody CarritoRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(carritoService.add(dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        carritoService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
