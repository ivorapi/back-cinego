package com.uade.demo.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.uade.demo.dto.PeliculaRequestDTO;
import com.uade.demo.dto.PeliculaResponseDTO;
import com.uade.demo.exception.ResourceNotFoundException;
import com.uade.demo.model.Peliculas;
import com.uade.demo.repository.PeliculaRepository;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class PeliculaService {

    private final PeliculaRepository peliculaRepository;

    public PeliculaService(PeliculaRepository peliculaRepository) {
        this.peliculaRepository = peliculaRepository;
    }

    public List<PeliculaResponseDTO> findAll() {
        return peliculaRepository.findAll().stream()
                .map(PeliculaResponseDTO::new)
                .toList();
    }

    public PeliculaResponseDTO findById(Long id) {
        return new PeliculaResponseDTO(buscarOFallar(id));
    }

    public PeliculaResponseDTO save(PeliculaRequestDTO dto) {
        validar(dto);
        Peliculas pelicula = new Peliculas();
        aplicarDatos(pelicula, dto);
        return new PeliculaResponseDTO(peliculaRepository.save(pelicula));
    }

    public PeliculaResponseDTO update(Long id, PeliculaRequestDTO dto) {
        validar(dto);
        Peliculas existente = buscarOFallar(id);
        aplicarDatos(existente, dto);
        return new PeliculaResponseDTO(peliculaRepository.save(existente));
    }

    public void deleteById(Long id) {
        peliculaRepository.deleteById(id);
    }

    public void deleteAll() {
        peliculaRepository.deleteAll();
    }

    private Peliculas buscarOFallar(Long id) {
        return peliculaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró la película con id: " + id));
    }

    private void validar(PeliculaRequestDTO dto) {
        if (dto.getTitulo() == null || dto.getTitulo().isBlank()) {
            throw new IllegalArgumentException("El título de la película es requerido");
        }
    }

    private void aplicarDatos(Peliculas pelicula, PeliculaRequestDTO dto) {
        pelicula.setTitulo(dto.getTitulo());
        pelicula.setDuracion(dto.getDuracion());
        pelicula.setClasificacion(dto.getClasificacion());
        pelicula.setSinopsis(dto.getSinopsis());
        pelicula.setPosterUrl(dto.getPosterUrl());
    }
}