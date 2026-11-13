package com.uade.demo.pricing;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.uade.demo.model.Descuento;
import com.uade.demo.model.Funcion;
import com.uade.demo.model.Peliculas;

class DescuentoFactoryTest {

    private final DescuentoFactory factory = new DescuentoFactory();
    private static final BigDecimal PRECIO = new BigDecimal("8000");

    @Test
    void sinDescuentosQuedaElPrecioBase() {
        Funcion funcion = funcionDe(pelicula(1L));

        assertThat(precioFinal(funcion, List.of())).isEqualByComparingTo("8000");
    }

    @Test
    void aplicaUnDescuentoParaTodasLasPeliculas() {
        Funcion funcion = funcionDe(pelicula(1L));

        assertThat(precioFinal(funcion, List.of(paraTodas("Estudiante", 10)))).isEqualByComparingTo("7200");
    }

    @Test
    void noAplicaUnDescuentoDeOtraPelicula() {
        Funcion funcion = funcionDe(pelicula(1L));

        assertThat(precioFinal(funcion, List.of(paraPeliculas("Estreno", 15, pelicula(2L)))))
                .isEqualByComparingTo("8000");
    }

    @Test
    void noSonAcumulables_seAplicaSoloElDeMayorPorcentaje() {
        Peliculas dune = pelicula(1L);
        Funcion funcion = funcionDe(dune);
        Descuento estudiante = paraTodas("Estudiante", 10);
        Descuento estreno = paraPeliculas("Estreno", 15, dune);

        // 15% sobre 8000, no 15% y después 10% (que daría 6120)
        assertThat(precioFinal(funcion, List.of(estudiante, estreno))).isEqualByComparingTo("6800");
        assertThat(factory.elegirDescuento(funcion, List.of(estudiante, estreno))).contains(estreno);
    }

    private BigDecimal precioFinal(Funcion funcion, List<Descuento> descuentos) {
        return factory.crearCalculador(funcion, descuentos).calcularPrecio(PRECIO);
    }

    private Peliculas pelicula(Long id) {
        Peliculas pelicula = new Peliculas();
        pelicula.setId(id);
        return pelicula;
    }

    private Funcion funcionDe(Peliculas pelicula) {
        Funcion funcion = new Funcion();
        funcion.setPelicula(pelicula);
        funcion.setPrecio(PRECIO);
        return funcion;
    }

    private Descuento paraTodas(String nombre, double porcentaje) {
        Descuento descuento = new Descuento();
        descuento.setNombre(nombre);
        descuento.setPorcentaje(porcentaje);
        descuento.setAplicaATodas(true);
        return descuento;
    }

    private Descuento paraPeliculas(String nombre, double porcentaje, Peliculas... peliculas) {
        Descuento descuento = new Descuento();
        descuento.setNombre(nombre);
        descuento.setPorcentaje(porcentaje);
        descuento.setPeliculas(List.of(peliculas));
        return descuento;
    }
}
