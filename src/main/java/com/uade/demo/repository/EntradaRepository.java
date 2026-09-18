package com.uade.demo.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.uade.demo.model.Entrada;
import com.uade.demo.model.EstadoReserva;

@Repository
public interface EntradaRepository extends JpaRepository<Entrada, Long> {
    List<Entrada> findByReservaId(Long reservaId);
    List<Entrada> findByFuncionId(Long funcionId);
    Optional<Entrada> findByCodigo(String codigo);

    @Query("""
        SELECT COUNT(e) > 0
        FROM Entrada e
        WHERE e.funcion.id = :funcionId
          AND e.asiento.id = :asientoId
          AND e.reserva.estado <> :estadoCancelada
        """)
    boolean existeEntradaActiva(
        @Param("funcionId") Long funcionId,
        @Param("asientoId") Long asientoId,
        @Param("estadoCancelada") EstadoReserva estadoCancelada);
}
