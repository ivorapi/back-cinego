package com.uade.demo.repository;
import java.util.List;
import java.time.LocalDateTime;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.uade.demo.model.Reserva;
import com.uade.demo.model.EstadoReserva;

@Repository 
public interface ReservaRepository extends JpaRepository<Reserva, Long> {
    List<Reserva> findByUsuario_Id(Long usuarioId);

    @Modifying
    @Query("UPDATE Reserva r SET r.estado = :cancelada WHERE r.estado = :pendiente AND r.creadaEn <= :limite")
    int cancelarVencidas(@Param("pendiente") EstadoReserva pendiente,
            @Param("cancelada") EstadoReserva cancelada, @Param("limite") LocalDateTime limite);

    @Modifying
    @Query("UPDATE Reserva r SET r.estado = :cancelada WHERE r.estado = :pendiente AND r.usuario.id = :usuarioId")
    int cancelarPendientesDeUsuario(@Param("pendiente") EstadoReserva pendiente,
            @Param("cancelada") EstadoReserva cancelada, @Param("usuarioId") Long usuarioId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE Reserva r SET r.estado = :confirmada WHERE r.id = :id AND r.estado = :pendiente AND r.creadaEn > :limite")
    int confirmarPendiente(@Param("id") Long id, @Param("pendiente") EstadoReserva pendiente,
            @Param("confirmada") EstadoReserva confirmada, @Param("limite") LocalDateTime limite);
}
