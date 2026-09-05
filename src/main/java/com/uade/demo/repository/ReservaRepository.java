package com.uade.demo.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import com.uade.demo.model.Reservas;

public interface ReservaRepository extends JpaRepository<Reservas, Long> {
}
