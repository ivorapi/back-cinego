package com.uade.demo.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "carrito", uniqueConstraints = {
        @UniqueConstraint(name = "uk_carrito_usuario_candy", columnNames = {"usuario_id", "candy_id"})
})
public class Carrito {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuarios usuario;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "candy_id", nullable = false)
    private Candy candy;

    @Column(nullable = false)
    private Integer cantidad;

    public Long getUsuarioId() {
        return usuario != null ? usuario.getId() : null;
    }

    public Long getCandyId() {
        return candy != null ? candy.getId() : null;
    }
}
