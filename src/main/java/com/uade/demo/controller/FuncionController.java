package com.uade.demo.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.uade.demo.service.FuncionService;

import com.uade.demo.model.Funcion;
import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;


@RestController
@RequestMapping("/api/funciones")
public class FuncionController {
    private final FuncionService funcionServices; 

    public FuncionController(FuncionService funcionServices) {
        this.funcionServices = funcionServices;
    }
    // devolver a todas las funciones
    // get localhost:8080/api/funciones
    @GetMapping
    public List<Funcion> getAllFunciones() {
        return funcionServices.getAllFunciones();
    }


    // eliminar funcion por id
    // delete localhost:8080/api/funciones/1
    @DeleteMapping("/{id}")
    public void deleteFuncion(@PathVariable Long id) {
        funcionServices.deleteFuncion(id);
    }
}