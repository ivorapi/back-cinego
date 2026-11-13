package com.uade.demo.pricing;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Component;

import com.uade.demo.model.Descuento;
import com.uade.demo.model.Funcion;

@Component
public class DescuentoFactory {

    // Los descuentos no son acumulables: de los que valen para la función se aplica
    // solo el de mayor porcentaje. Si ninguno vale, queda el precio base.
    public CalculadorPrecio crearCalculador(Funcion funcion, List<Descuento> descuentosActivos) {
        CalculadorPrecio calculador = new PrecioBase();

        return elegirDescuento(funcion, descuentosActivos)
                .<CalculadorPrecio>map(descuento -> new DescuentoPorcentajeDecorator(calculador, descuento))
                .orElse(calculador);
    }

    public Optional<Descuento> elegirDescuento(Funcion funcion, List<Descuento> descuentosActivos) {
        return descuentosActivos.stream()
                .filter(descuento -> aplicaA(descuento, funcion))
                .max(Comparator.comparingDouble(Descuento::getPorcentaje));
    }

    private boolean aplicaA(Descuento descuento, Funcion funcion) {
        return descuento.isAplicaATodas()
                || descuento.getPeliculas().stream()
                        .anyMatch(pelicula -> pelicula.getId().equals(funcion.getPeliculaId()));
    }
}
