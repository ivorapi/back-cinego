package com.uade.demo.pricing;

import java.util.List;

import org.springframework.stereotype.Component;

import com.uade.demo.model.Descuento;
import com.uade.demo.model.Funcion;

@Component
public class DescuentoFactory {

    public CalculadorPrecio crearCalculador(Funcion funcion, List<Descuento> descuentosActivos) {
        CalculadorPrecio calculador = new PrecioBase();

        for (Descuento descuento : descuentosActivos) {
            boolean aplica = descuento.isAplicaATodas()
                    || descuento.getPeliculas().stream()
                            .anyMatch(pelicula -> pelicula.getId().equals(funcion.getPeliculaId()));

            if (aplica) {
                calculador = new DescuentoPorcentajeDecorator(calculador, descuento);
            }
        }

        return calculador;
    }
}
