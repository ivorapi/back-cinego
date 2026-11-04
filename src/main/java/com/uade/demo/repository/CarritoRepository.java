package com.uade.demo.repository;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.uade.demo.model.Carrito;

@Repository
public interface CarritoRepository extends JpaRepository<Carrito, Long> {
    List<Carrito> findByUsuario_Id(Long usuarioId);

    Optional<Carrito> findByUsuario_IdAndCandy_Id(Long usuarioId, Long candyId);
}
