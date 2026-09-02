package com.uade.demo.pricing;

import java.math.BigDecimal;

import com.uade.demo.model.Descuento;

public class DescuentoPorcentajeDecorator extends DescuentoDecorator {

    private final Descuento descuento;

    public DescuentoPorcentajeDecorator(CalculadorPrecio componente, Descuento descuento) {
        super(componente);
        this.descuento = descuento;
    }

    @Override
    public BigDecimal calcularPrecio(BigDecimal base) {
        BigDecimal precioPrevio = componente.calcularPrecio(base);
        BigDecimal factor = BigDecimal.ONE.subtract(
                BigDecimal.valueOf(descuento.getPorcentaje()).divide(BigDecimal.valueOf(100)));
        return precioPrevio.multiply(factor);
    }
}
