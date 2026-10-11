package com.uade.demo.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.uade.demo.dto.AsociarPeliculasRequestDTO;
import com.uade.demo.dto.CambiarEstadoRequestDTO;
import com.uade.demo.dto.DescuentoRequestDTO;
import com.uade.demo.dto.DescuentoResponseDTO;
import com.uade.demo.service.DescuentoService;

@RestController
@RequestMapping("/api/descuentos")
public class DescuentoController {

    private final DescuentoService descuentoService;

    public DescuentoController(DescuentoService descuentoService) {
        this.descuentoService = descuentoService;
    }

    @PostMapping
    public ResponseEntity<DescuentoResponseDTO> crear(@RequestBody DescuentoRequestDTO body) {
        DescuentoResponseDTO creado = descuentoService.toResponseDTO(descuentoService.crear(body));
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }

    @GetMapping
    public List<DescuentoResponseDTO> listar() {
        return descuentoService.listar();
    }

    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Long id) {
        descuentoService.eliminar(id);
    }

    @PatchMapping("/{id}/estado")
    public ResponseEntity<DescuentoResponseDTO> cambiarEstado(@PathVariable Long id,
            @RequestBody CambiarEstadoRequestDTO body) {
        DescuentoResponseDTO actualizado = descuentoService.toResponseDTO(
                descuentoService.cambiarEstado(id, body.isActivo()));
        return ResponseEntity.ok(actualizado);
    }

    @PatchMapping("/{id}/peliculas")
    public ResponseEntity<DescuentoResponseDTO> asociarPeliculas(@PathVariable Long id,
            @RequestBody AsociarPeliculasRequestDTO body) {
        DescuentoResponseDTO actualizado = descuentoService.toResponseDTO(
                descuentoService.asociarPeliculas(id, body.getPeliculaIds()));
        return ResponseEntity.ok(actualizado);
    }
}
