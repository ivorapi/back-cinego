package com.uade.demo.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.uade.demo.model.Entrada;

public interface EntradaRepository extends JpaRepository<Entrada, Long> {
    List<Entrada> findByReservaId(Long reservaId);
    List<Entrada> findByFuncionId(Long funcionId);
    Optional<Entrada> findByCodigo(String codigo);
}
