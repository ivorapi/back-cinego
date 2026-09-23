package com.uade.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.uade.demo.model.Descuento;

public interface DescuentoRepository extends JpaRepository<Descuento, Long> {
    List<Descuento> findByActivoTrue();
}
