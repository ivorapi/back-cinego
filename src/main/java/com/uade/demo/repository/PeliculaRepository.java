package com.uade.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.uade.demo.model.Peliculas;

public interface PeliculaRepository extends JpaRepository<Peliculas, Long> {
}
