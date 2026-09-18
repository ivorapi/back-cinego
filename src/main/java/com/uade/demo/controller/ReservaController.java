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

import com.uade.demo.dto.ReservaRequestDTO;
import com.uade.demo.model.Reservas;
import com.uade.demo.service.ReservaService;

@RestController
@RequestMapping("/api/reserva")
public class ReservaController {
    private final ReservaService reservaServices;

    public ReservaController(ReservaService reservaServices) {
        this.reservaServices = reservaServices;
    }

    @GetMapping("/{id}")
    public List<Reservas> getReservaById(@PathVariable Long id) {
        return reservaServices.getReservaByid(id).map(List::of)
                .orElseThrow(() -> new RuntimeException("Reserva not found with id: " + id));
    }

    @PostMapping({"", "/"})
    public ResponseEntity<Reservas> createReserva(@RequestBody ReservaRequestDTO request) {
        if (request.getUsuarioId() == null || request.getFuncionId() == null) {
            throw new IllegalArgumentException("usuarioId y funcionId son requeridos");
        }

        Reservas creada = reservaServices.createReserva(request.getUsuarioId(), request.getFuncionId());
        return ResponseEntity.status(HttpStatus.CREATED).body(creada);
    }

    @DeleteMapping("/{id}")
    public void deleteReserva(@PathVariable Long id) {
        reservaServices.deleteReservas(id);
    }
}