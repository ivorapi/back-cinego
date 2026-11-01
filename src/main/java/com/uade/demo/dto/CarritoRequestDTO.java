package com.uade.demo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CarritoRequestDTO {
    @Schema(description = "Identificador del producto de candy a agregar", example = "1")
    @NotNull(message = "El producto es obligatorio")
    private Long candyId;

    @Schema(description = "Cantidad de unidades a agregar", example = "2")
    @NotNull(message = "La cantidad es obligatoria")
    @Min(value = 1, message = "La cantidad debe ser al menos 1")
    private Integer cantidad;
}
