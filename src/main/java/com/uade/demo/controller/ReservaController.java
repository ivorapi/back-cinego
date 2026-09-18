package com.uade.demo.controller;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.uade.demo.dto.ReservaRequestDTO;
import com.uade.demo.dto.ReservaResponseDTO;
import com.uade.demo.service.ReservaService;

import jakarta.validation.Valid;

import com.uade.demo.model.Reserva;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PostMapping;

@RestController
@RequestMapping("/api/reserva")
public class ReservaController {
    private final ReservaService reservaServices; 

    public ReservaController(ReservaService reservaServices) {
        this.reservaServices = reservaServices;
    }

    @GetMapping("/{id}")
    public Reserva getReservaById(@PathVariable Long id) {
        return reservaServices.getReservaByid(id)
                .orElseThrow(() -> new RuntimeException("Reserva not found with id: " + id));
    }
    
    @PostMapping
    public ResponseEntity<ReservaResponseDTO> createReserva(@Valid @RequestBody ReservaRequestDTO reservaRequest) {

        if (reservaRequest.getFuncionId() == null) {
            throw new IllegalArgumentException("La función es obligatoria");
        }

        List<Long> asientoIds = reservaRequest.getAsientoId();
        if (asientoIds == null || asientoIds.isEmpty()) {
            throw new IllegalArgumentException("Debés seleccionar al menos un asiento");
        }

        Set<Long> asientoIdsUnicos = new HashSet<>(asientoIds);
        if (asientoIdsUnicos.size() != asientoIds.size()) {
            throw new IllegalArgumentException(
                    "No podés repetir asientos en una misma reserva");
        }

        ReservaResponseDTO reserva = reservaServices.createReserva(reservaRequest);

        return ResponseEntity.status(HttpStatus.CREATED).body(reserva);
    }
    
    @DeleteMapping("/{id}")
    public void deleteReserva(@PathVariable Long id) {
        reservaServices.deleteReserva(id);
    }

    @GetMapping("/mis-reservas")
    public List<Reserva> getMisReservas() {
        return reservaServices.getReservasDelUsuarioAutenticado();
    }
    
    @PostMapping("/{id}/cancelar")
    public ResponseEntity<Reserva> cancelarReserva(@PathVariable Long id) {
        Reserva reservaCancelada = reservaServices.cancelarReserva(id);
        return ResponseEntity.ok(reservaCancelada);
    }
}
