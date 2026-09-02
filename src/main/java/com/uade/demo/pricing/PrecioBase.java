package com.uade.demo.pricing;

import java.math.BigDecimal;

public class PrecioBase implements CalculadorPrecio {

    @Override
    public BigDecimal calcularPrecio(BigDecimal base) {
        return base;
    }
}
