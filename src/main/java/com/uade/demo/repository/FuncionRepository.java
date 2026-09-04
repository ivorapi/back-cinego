package com.uade.demo.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import com.uade.demo.model.Funcion;

public interface FuncionRepository extends JpaRepository<Funcion, Long> {
}
