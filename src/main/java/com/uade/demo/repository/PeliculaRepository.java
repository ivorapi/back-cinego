package com.uade.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.uade.demo.model.Peliculas;

@Repository
public interface PeliculaRepository extends JpaRepository<Peliculas, Long> {
}
