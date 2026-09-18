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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PostMapping;

@RestController
@RequestMapping("/api/reservas")
@Tag(name = "Reservas", description = "Creación, consulta y cancelación de reservas")
public class ReservaController {
    private final ReservaService reservaServices; 

    public ReservaController(ReservaService reservaServices) {
        this.reservaServices = reservaServices;
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener una reserva por id", description = "El cliente sólo puede consultar una reserva propia. ADMIN y SUPER_ADMIN pueden consultar cualquier reserva.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Reserva encontrada"),
            @ApiResponse(responseCode = "403", description = "La reserva pertenece a otro usuario"),
            @ApiResponse(responseCode = "404", description = "Reserva inexistente")
    })
    public ResponseEntity<ReservaResponseDTO> getReservaById(@PathVariable Long id) {
        return ResponseEntity.ok(reservaServices.getReservaById(id));
    }
    
    @PostMapping
    @Operation(summary = "Crear una reserva", description = "Crea una reserva para el usuario autenticado y genera una entrada por cada asiento seleccionado.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Reserva creada"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos o asientos de otra sala"),
            @ApiResponse(responseCode = "404", description = "Función o asiento inexistente"),
            @ApiResponse(responseCode = "409", description = "Uno o más asientos ya están ocupados")
    })
    public ResponseEntity<ReservaResponseDTO> createReserva(@Valid @RequestBody ReservaRequestDTO reservaRequest) {
        ReservaResponseDTO reserva = reservaServices.createReserva(reservaRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(reserva);
    }

    @GetMapping("/mis-reservas")
    @Operation(summary = "Listar mis reservas", description = "Devuelve las reservas del usuario identificado por el JWT.")
    @ApiResponse(responseCode = "200", description = "Reservas del usuario")
    public List<ReservaResponseDTO> getMisReservas() {
        return reservaServices.getMisReservas();
    }
    
    @PatchMapping("/{id}/cancelar")
    @Operation(summary = "Cancelar una reserva", description = "Cancela una reserva propia. ADMIN y SUPER_ADMIN pueden cancelar cualquier reserva.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Reserva cancelada"),
            @ApiResponse(responseCode = "400", description = "La reserva ya estaba cancelada"),
            @ApiResponse(responseCode = "403", description = "La reserva pertenece a otro usuario"),
            @ApiResponse(responseCode = "404", description = "Reserva inexistente")
    })
    public ResponseEntity<ReservaResponseDTO> cancelarReserva(@PathVariable Long id) {
        ReservaResponseDTO reservaCancelada = reservaServices.cancelarReserva(id);
        return ResponseEntity.ok(reservaCancelada);
    }
}
