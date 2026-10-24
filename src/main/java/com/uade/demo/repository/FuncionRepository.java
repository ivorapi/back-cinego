package com.uade.demo.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.uade.demo.model.Funcion;

@Repository 
public interface FuncionRepository extends JpaRepository<Funcion, Long> {
    @org.springframework.data.jpa.repository.Lock(jakarta.persistence.LockModeType.PESSIMISTIC_WRITE)
    @org.springframework.data.jpa.repository.Query("SELECT f FROM Funcion f WHERE f.id = :id")
    java.util.Optional<Funcion> findByIdForUpdate(@org.springframework.data.repository.query.Param("id") Long id);
}
