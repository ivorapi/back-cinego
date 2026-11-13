package com.uade.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.uade.demo.model.Peliculas;

@Repository
public interface PeliculaRepository extends JpaRepository<Peliculas, Long> {

    @Query("SELECT p FROM Peliculas p WHERE " +
           "(:titulo IS NULL OR :titulo = '' OR LOWER(p.titulo) LIKE LOWER(CONCAT('%', :titulo, '%'))) AND " +
           "(:genero IS NULL OR :genero = '' OR LOWER(p.genero) LIKE LOWER(CONCAT('%', :genero, '%')))")
    List<Peliculas> buscarConFiltros(@Param("titulo") String titulo, @Param("genero") String genero);
}
