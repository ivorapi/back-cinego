package com.uade.demo.dto;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

import com.uade.demo.model.EstadoReserva;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data 
@Schema(description = "Datos de una reserva y sus entradas")
public class ReservaResponseDTO {
    @Schema(example = "10")
    private Long idReserva;

    @Schema(example = "1")
    private Long idFuncion;

    @Schema(example = "PENDIENTE")
    private EstadoReserva estado;

    @Schema(example = "2026-09-18T20:30:00-03:00")
    private OffsetDateTime fechaReserva;

    @Schema(example = "[20, 21]")
    private List<Long> idsEntradas;

    private List<AsientoReservaDTO> asientos;

    @Schema(example = "17000.00")
    private BigDecimal total;
}
