package com.uade.demo.dto;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ReservaRequestDTO {
    @Schema(description = "Identificador de la función a reservar", example = "1")
    @NotNull(message = "La función es obligatoria")
    private Long funcionId;

    @Schema(description = "Identificadores de los asientos elegidos", example = "[3, 4]")
    @NotEmpty(message = "Debés seleccionar al menos un asiento")
    private List<@NotNull(message = "El id de cada asiento es obligatorio") Long> asientoIds;
}
