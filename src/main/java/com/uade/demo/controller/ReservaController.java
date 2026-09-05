package com.uade.demo.controller;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.uade.demo.service.ReservaService;
import com.uade.demo.model.Reservas;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/api/reserva")
public class ReservaController {
    private final ReservaService reservaServices; 

    public ReservaController(ReservaService reservaServices) {
        this.reservaServices = reservaServices;
    }
    // devolver a todas las funciones
    // get localhost:8080/api/funciones
    @GetMapping("/{id}")
    public List<Reservas> getReservaById(@PathVariable Long id) {
        return reservaServices.getReservaByid(id).map(List::of)
                .orElseThrow(() -> new RuntimeException("Reserva not found with id: " + id));
    }

    @PostMapping("/")
    public String createReserva(@RequestBody String entity) {
        reservaServices.createReserva(1L, 1L, List.of(1L));
        
        return entity;
    }
    

    // eliminar funcion por id
    // delete localhost:8080/api/funciones/1
    @DeleteMapping("/{id}")
    public void deleteReserva(@PathVariable Long id) {
        reservaServices.deleteReservas(id);
    }
}