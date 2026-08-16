package com.uade.demo.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.uade.demo.service.FuncionServices;

import com.uade.demo.model.Funcion;
import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;


@RestController
@RequestMapping("/funciones")
public class FuncionController {
    private final FuncionServices funcionServices; 

    public FuncionController(FuncionServices funcionServices) {
        this.funcionServices = funcionServices;
    }

    @GetMapping
    public List<Funcion> getAllFunciones() {
        return funcionServices.getAllFunciones();
    }
}