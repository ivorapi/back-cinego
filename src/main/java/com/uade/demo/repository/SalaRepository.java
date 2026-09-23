package com.uade.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.uade.demo.model.Salas;

public interface SalaRepository extends JpaRepository<Salas, Long> {
}