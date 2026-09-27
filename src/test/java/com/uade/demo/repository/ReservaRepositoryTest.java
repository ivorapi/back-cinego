package com.uade.demo.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import com.uade.demo.model.Funcion;
import com.uade.demo.model.Peliculas;
import com.uade.demo.model.Reserva;
import com.uade.demo.model.Rol;
import com.uade.demo.model.Salas;
import com.uade.demo.model.Usuarios;

@DataJpaTest
class ReservaRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ReservaRepository reservaRepository;

    @Test
    void findByUsuarioId_devuelveSoloLasReservasDelUsuario() {
        Usuarios usuarioConReserva = persistirUsuario("cliente1@cinego.test");
        Usuarios otroUsuario = persistirUsuario("cliente2@cinego.test");
        Funcion funcion = persistirFuncion();

        persistirReserva(usuarioConReserva, funcion);
        persistirReserva(otroUsuario, funcion);
        entityManager.flush();
        entityManager.clear();

        var reservas = reservaRepository.findByUsuario_Id(usuarioConReserva.getId());

        assertThat(reservas)
                .hasSize(1)
                .allSatisfy(reserva -> assertThat(reserva.getUsuarioId()).isEqualTo(usuarioConReserva.getId()));
    }

    @Test
    void findByUsuarioId_sinReservasDevuelveUnaListaVacia() {
        Usuarios usuario = persistirUsuario("sin-reservas@cinego.test");

        var reservas = reservaRepository.findByUsuario_Id(usuario.getId());

        assertThat(reservas).isEmpty();
    }

    @Test
    void findByUsuarioId_usuarioInexistenteDevuelveUnaListaVacia() {
        var reservas = reservaRepository.findByUsuario_Id(9999L);

        assertThat(reservas).isEmpty();
    }

    private Usuarios persistirUsuario(String email) {
        Usuarios usuario = new Usuarios();
        usuario.setEmail(email);
        usuario.setNombre("Cliente");
        usuario.setApellido("Prueba");
        usuario.setPasswordHash("hash-de-prueba");
        usuario.setRol(Rol.CLIENTE);
        return entityManager.persistAndFlush(usuario);
    }

    private Funcion persistirFuncion() {
        Peliculas pelicula = new Peliculas();
        pelicula.setTitulo("Película de prueba");
        pelicula.setDuracion(120);
        pelicula.setClasificacion(8.0);
        entityManager.persist(pelicula);

        Salas sala = new Salas();
        sala.setNombre("Sala de prueba");
        entityManager.persist(sala);

        Funcion funcion = new Funcion();
        funcion.setPelicula(pelicula);
        funcion.setSala(sala);
        funcion.setHorarioInicio(LocalDateTime.of(2026, 10, 1, 20, 0));
        funcion.setPrecio(BigDecimal.valueOf(8000));
        return entityManager.persistAndFlush(funcion);
    }

    private void persistirReserva(Usuarios usuario, Funcion funcion) {
        Reserva reserva = new Reserva();
        reserva.setUsuario(usuario);
        reserva.setFuncion(funcion);
        reserva.setTotal(BigDecimal.valueOf(8000));
        entityManager.persist(reserva);
    }
}
