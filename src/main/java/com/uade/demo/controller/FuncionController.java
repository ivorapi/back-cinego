package com.uade.demo.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.uade.demo.service.FuncionServices;

import com.uade.demo.model.Funcion;
import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;


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
    public List<Funcion> getAllFunciones() {
        return funcionServices.getAllFunciones();
    }

    // devolver funcion por id
    // get localhost:8080/api/funciones/1
    @GetMapping("/{id}")
    public Funcion getFuncionById(@PathVariable Long id) {
        return funcionServices.getFuncionById(id);
    }

    // crear funcion
    // post localhost:8080/api/funciones
    @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN')")
    @PostMapping
    public Funcion createFuncion(@RequestBody Funcion funcion) {
        return funcionServices.saveFuncion(funcion);
    }

    // actualizar funcion por id
    // put localhost:8080/api/funciones/1
    @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN')")
    @PutMapping("/{id}")
    public Funcion updateFuncion(@PathVariable Long id, @RequestBody Funcion funcion) {
        funcion.setId(id);
        return funcionServices.saveFuncion(funcion);
    }

    // eliminar funcion por id
    // delete localhost:8080/api/funciones/1
    @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN')")
    @DeleteMapping("/{id}")
    public void deleteFuncion(@PathVariable Long id) {
        funcionServices.deleteFuncion(id);
    }

    // eliminar todas las funciones
    // delete localhost:8080/api/funciones
    @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN')")
    @DeleteMapping
    public void deleteAllFunciones() {
        funcionServices.deleteAllFunciones();
    }
}