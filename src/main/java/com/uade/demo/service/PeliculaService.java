package com.uade.demo.service;

import java.util.List;

import org.springframework.stereotype.Service;

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

    public List<Peliculas> findAll() {
        return peliculaRepository.findAll();
    }

    public Peliculas findById(Long id) {
        return peliculaRepository.findById(id).orElse(null);
    }

    public Peliculas save(Peliculas pelicula) {
        return peliculaRepository.save(pelicula);
    }

    public void deleteById(Long id) {
        peliculaRepository.deleteById(id);
    }

    public void deleteAll() {
        peliculaRepository.deleteAll();
    }
}