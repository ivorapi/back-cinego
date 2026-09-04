package com.uade.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.uade.demo.model.Funcion;

@Repository
public interface FuncionRepository extends JpaRepository<Funcion, Long> {
}
