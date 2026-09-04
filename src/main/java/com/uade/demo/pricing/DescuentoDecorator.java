package com.uade.demo.pricing;

public abstract class DescuentoDecorator implements CalculadorPrecio {

    protected CalculadorPrecio componente;

    protected DescuentoDecorator(CalculadorPrecio componente) {
        this.componente = componente;
    }
}
