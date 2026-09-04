package com.uade.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.uade.demo.model.Salas;

@Repository
public interface SalaRepository extends JpaRepository<Salas, Long> {
}