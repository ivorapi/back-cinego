package com.uade.demo.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "reservas")
public class Reservas {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuarios usuario;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "funcion_id", nullable = false)
    private Funcion funcion;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoReserva estado = EstadoReserva.PENDIENTE;

    @Column(name = "creada_en", nullable = false, updatable = false)
    private LocalDateTime creadaEn;

    @Column(precision = 10, scale = 2)
    private BigDecimal total;

    @JsonIgnore
    @OneToMany(mappedBy = "reserva")
    private List<Entrada> entradas = new ArrayList<>();

    @PrePersist
    void asignarValoresIniciales() {
        if (creadaEn == null) {
            creadaEn = LocalDateTime.now();
        }
        if (estado == null) {
            estado = EstadoReserva.PENDIENTE;
        }
    }

    public BigDecimal getTotal() {
        if (total != null) {
            return total;
        }
        if (entradas != null && !entradas.isEmpty()) {
            return entradas.stream()
                    .map(Entrada::getPrecio)
                    .filter(java.util.Objects::nonNull)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
        }
        return BigDecimal.ZERO;
    }

    public Long getUsuarioId() {
        return usuario != null ? usuario.getId() : null;
    }

    public Long getFuncionId() {
        return funcion != null ? funcion.getId() : null;
    }
}
