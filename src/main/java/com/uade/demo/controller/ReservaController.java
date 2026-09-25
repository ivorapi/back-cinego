package com.uade.demo.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.uade.demo.dto.ReservaRequestDTO;
import com.uade.demo.dto.ReservaResponseDTO;
import com.uade.demo.service.ReservaService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PostMapping;

@RestController
@RequestMapping("/api/reservas")
public class ReservaController {
    private final ReservaService reservaServices; 

    public ReservaController(ReservaService reservaServices) {
        this.reservaServices = reservaServices;
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReservaResponseDTO> getReservaById(@PathVariable Long id) {
        return ResponseEntity.ok(reservaServices.getReservaById(id));
    }
    
    @PostMapping
    public ResponseEntity<ReservaResponseDTO> createReserva(@Valid @RequestBody ReservaRequestDTO reservaRequest) {
        ReservaResponseDTO reserva = reservaServices.createReserva(reservaRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(reserva);
    }

    @GetMapping("/mis-reservas")
    public List<ReservaResponseDTO> getMisReservas() {
        return reservaServices.getMisReservas();
    }
    
    @PatchMapping("/{id}/cancelar")
    public ResponseEntity<ReservaResponseDTO> cancelarReserva(@PathVariable Long id) {
        ReservaResponseDTO reservaCancelada = reservaServices.cancelarReserva(id);
        return ResponseEntity.ok(reservaCancelada);
    }
}
