package com.uade.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.uade.demo.model.Asiento;

public interface AsientoRepository extends JpaRepository<Asiento, Long> {
    List<Asiento> findBySalaId(Long salaId);
}
