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

import com.uade.demo.dto.funcion.FuncionRequestDTO;
import com.uade.demo.dto.funcion.FuncionResponseDTO;
import com.uade.demo.service.FuncionServices;

@RestController
@RequestMapping("/api/funciones")
public class FuncionController {
    private final FuncionServices funcionServices;

    public FuncionController(FuncionServices funcionServices) {
        this.funcionServices = funcionServices;
    }

    // devolver a todas las funciones
    // get localhost:8080/api/funciones
    @GetMapping
    public List<FuncionResponseDTO> getAllFunciones() {
        return funcionServices.getAllFunciones();
    }

    // devolver funcion por id
    // get localhost:8080/api/funciones/1
    @GetMapping("/{id}")
    public ResponseEntity<FuncionResponseDTO> getFuncionById(@PathVariable Long id) {
        return ResponseEntity.ok(funcionServices.getFuncionById(id));
    }

    // crear funcion
    // post localhost:8080/api/funciones
    @PostMapping
    public ResponseEntity<FuncionResponseDTO> createFuncion(@RequestBody FuncionRequestDTO funcionRequestDTO) {
        if (funcionRequestDTO.getPeliculaId() == null || funcionRequestDTO.getSalaId() == null) {
            throw new IllegalArgumentException("peliculaId y salaId son requeridos");
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(funcionServices.crearFuncion(funcionRequestDTO));
    }

    // actualizar funcion por id
    // put localhost:8080/api/funciones/1
    @PutMapping("/{id}")
    public FuncionResponseDTO updateFuncion(@PathVariable Long id, @RequestBody FuncionRequestDTO funcionRequestDTO) {
        return funcionServices.actualizarFuncion(id, funcionRequestDTO);
    }

    // eliminar funcion por id
    // delete localhost:8080/api/funciones/1
    @DeleteMapping("/{id}")
    public void deleteFuncion(@PathVariable Long id) {
        funcionServices.deleteFuncion(id);
    }

    // eliminar todas las funciones
    // delete localhost:8080/api/funciones
    @DeleteMapping
    public void deleteAllFunciones() {
        funcionServices.deleteAllFunciones();
    }
}