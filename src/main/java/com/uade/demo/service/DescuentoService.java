package com.uade.demo.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.uade.demo.dto.DescuentoRequestDTO;
import com.uade.demo.dto.DescuentoResponseDTO;
import com.uade.demo.exception.ResourceNotFoundException;
import com.uade.demo.model.Descuento;
import com.uade.demo.model.Funcion;
import com.uade.demo.model.Peliculas;
import com.uade.demo.pricing.DescuentoFactory;
import com.uade.demo.repository.DescuentoRepository;
import com.uade.demo.repository.PeliculaRepository;

@Service
@Transactional
public class DescuentoService {

    private final DescuentoRepository descuentoRepository;
    private final PeliculaRepository peliculaRepository;
    private final DescuentoFactory descuentoFactory;

    public DescuentoService(DescuentoRepository descuentoRepository, PeliculaRepository peliculaRepository,
            DescuentoFactory descuentoFactory) {
        this.descuentoRepository = descuentoRepository;
        this.peliculaRepository = peliculaRepository;
        this.descuentoFactory = descuentoFactory;
    }

    public Descuento crear(DescuentoRequestDTO body) {
        validar(body);
        Descuento descuento = new Descuento();
        aplicarDatos(descuento, body);
        return descuentoRepository.save(descuento);
    }

    public Descuento actualizar(Long id, DescuentoRequestDTO body) {
        validar(body);
        Descuento descuento = obtenerODisparar(id);
        aplicarDatos(descuento, body);
        return descuentoRepository.save(descuento);
    }

    public void eliminar(Long id) {
        if (!descuentoRepository.existsById(id)) {
            throw new ResourceNotFoundException("No se encontró el descuento con id: " + id);
        }
        descuentoRepository.deleteById(id);
    }

    public Descuento cambiarEstado(Long id, boolean activo) {
        Descuento descuento = obtenerODisparar(id);
        descuento.setActivo(activo);
        return descuentoRepository.save(descuento);
    }

    public Descuento asociarPeliculas(Long id, List<Long> peliculaIds) {
        Descuento descuento = obtenerODisparar(id);
        List<Peliculas> peliculas = peliculaIds == null ? List.of() : peliculaRepository.findAllById(peliculaIds);
        descuento.setPeliculas(peliculas);
        return descuentoRepository.save(descuento);
    }

    @Transactional(readOnly = true)
    public List<DescuentoResponseDTO> listar() {
        return descuentoRepository.findAll().stream()
                .map(this::toResponseDTO)
                .toList();
    }

    public BigDecimal calcularPrecioFinal(Funcion funcion) {
        List<Descuento> descuentosActivos = descuentoRepository.findByActivoTrue();
        return descuentoFactory.crearCalculador(funcion, descuentosActivos)
                .calcularPrecio(funcion.getPrecio())
                .setScale(2, RoundingMode.HALF_UP);
    }

    public DescuentoResponseDTO toResponseDTO(Descuento descuento) {
        List<String> peliculaNombres = descuento.getPeliculas().stream()
                .map(Peliculas::getTitulo)
                .toList();
        List<Long> peliculaIds = descuento.getPeliculas().stream()
                .map(Peliculas::getId)
                .toList();
        return new DescuentoResponseDTO(
                descuento.getId(),
                descuento.getNombre(),
                descuento.getPorcentaje(),
                descuento.isActivo(),
                descuento.isAplicaATodas(),
                peliculaNombres,
                peliculaIds);
    }

    private void validar(DescuentoRequestDTO body) {
        if (body.getNombre() == null || body.getNombre().isBlank()) {
            throw new IllegalArgumentException("El nombre del descuento es requerido");
        }
        if (body.getPorcentaje() < 0 || body.getPorcentaje() > 100) {
            throw new IllegalArgumentException("El porcentaje debe estar entre 0 y 100");
        }
    }

    // Lo comparten crear y actualizar. Si aplica a todas las películas, la lista queda vacía
    private void aplicarDatos(Descuento descuento, DescuentoRequestDTO body) {
        descuento.setNombre(body.getNombre().trim());
        descuento.setPorcentaje(body.getPorcentaje());
        descuento.setAplicaATodas(body.isAplicaATodas());

        List<Long> peliculaIds = body.getPeliculaIds();
        if (body.isAplicaATodas() || peliculaIds == null || peliculaIds.isEmpty()) {
            descuento.setPeliculas(new ArrayList<>());
        } else {
            descuento.setPeliculas(new ArrayList<>(peliculaRepository.findAllById(peliculaIds)));
        }

        // null: al crear queda activa (el valor por defecto) y al editar no cambia
        if (body.getActivo() != null) {
            descuento.setActivo(body.getActivo());
        }
    }

    private Descuento obtenerODisparar(Long id) {
        return descuentoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró el descuento con id: " + id));
    }
}
