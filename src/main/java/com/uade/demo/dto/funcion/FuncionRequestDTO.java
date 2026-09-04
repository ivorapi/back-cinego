package com.uade.demo.dto.funcion;
 
import java.math.BigDecimal;
import java.time.LocalDateTime;
 
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
 
// DTO de entrada: lo que el cliente puede enviar al crear o editar una Funcion.
// Solo pide los ids de pelicula y sala (no las entidades completas), y no
// incluye el id de la funcion ni datos de solo lectura como los nombres.
@Getter
@Setter
@NoArgsConstructor
public class FuncionRequestDTO {
    private Long peliculaId;
    private Long salaId;
    private LocalDateTime horarioInicio;
    private BigDecimal precio;
}
