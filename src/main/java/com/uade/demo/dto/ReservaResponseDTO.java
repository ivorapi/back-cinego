package com.uade.demo.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.uade.demo.model.Entrada;
import com.uade.demo.model.EstadoReserva;
import com.uade.demo.model.Funcion;
import com.uade.demo.model.Reserva;

import lombok.Data;

@Data 
public class ReservaResponseDTO {
    Long reserva;
    Long funcion;
    EstadoReserva estado;
    LocalDateTime fechaReserva;
    List<Long> entradas;
    BigDecimal total;
}
