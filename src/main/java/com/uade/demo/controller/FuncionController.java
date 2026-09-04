package com.uade.demo.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.uade.demo.service.FuncionService;

import com.uade.demo.model.Funcion;
import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/api/funciones")
public class FuncionController {
    private final FuncionService funcionService;

    public FuncionController(FuncionService funcionService) {
        this.funcionService = funcionService;
    }
    // devolver a todas las funciones
    // get localhost:8080/api/funciones
    @GetMapping
    public List<Funcion> getAllFunciones() {
        return funcionService.getAllFunciones();
    }

    // devolver funcion por id
    // get localhost:8080/api/funciones/1
    @GetMapping("/{id}")
    public Funcion getFuncionById(@PathVariable Long id) {
        return funcionService.getFuncionById(id);
    }

    // crear funcion
    // post localhost:8080/api/funciones
    @PostMapping
    public Funcion createFuncion(@RequestBody Funcion funcion) {
        return funcionService.saveFuncion(funcion);
    }

    // actualizar funcion por id
    // put localhost:8080/api/funciones/1
    @PutMapping("/{id}")
    public Funcion updateFuncion(@PathVariable Long id, @RequestBody Funcion funcion) {
        funcion.setId(id);
        return funcionService.saveFuncion(funcion);
    }

    // eliminar funcion por id
    // delete localhost:8080/api/funciones/1
    @DeleteMapping("/{id}")
    public void deleteFuncion(@PathVariable Long id) {
        funcionService.deleteFuncion(id);
    }

    // eliminar todas las funciones
    // delete localhost:8080/api/funciones
    @DeleteMapping
    public void deleteAllFunciones() {
        funcionService.deleteAllFunciones();
    }
}
