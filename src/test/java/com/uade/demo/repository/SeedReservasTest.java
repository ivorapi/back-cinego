package com.uade.demo.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.uade.demo.service.FuncionService;

@SpringBootTest(properties = {
    "spring.datasource.url=jdbc:h2:mem:seed-reservas;MODE=MySQL;DB_CLOSE_DELAY=-1",
    "spring.datasource.driver-class-name=org.h2.Driver", "spring.datasource.username=sa",
    "spring.datasource.password=", "spring.jpa.hibernate.ddl-auto=create-drop"
})
class SeedReservasTest {
    @Autowired JdbcTemplate jdbc;
    @Autowired FuncionService funciones;
    @Autowired PasswordEncoder passwords;

    @Test
    void ejemplosCoherentesConElMapaDeAsientosYLasReservas() throws Exception {
        String sql = Files.readString(Path.of("initdb/01-schema-and-seed.sql"));
        sql = sql.substring(sql.indexOf("INSERT INTO usuarios"), sql.indexOf("INSERT INTO descuentos"));
        for (String statement : sql.split(";")) {
            if (!statement.isBlank()) jdbc.execute(statement);
        }
        assertThat(funciones.getAsientos(1L)).hasSize(24);
        assertThat(funciones.getAsientos(1L).stream().filter(a -> a.ocupado()).map(a -> a.id()))
            .containsExactly(1L, 2L, 3L);
        assertThat(funciones.getAsientos(3L).stream().filter(a -> a.ocupado()).map(a -> a.id()))
            .containsExactly(4L);
        assertThat(funciones.getAsientos(2L)).hasSize(24);
        assertThat(funciones.getAsientos(4L)).allMatch(a -> !a.ocupado());
        assertThat(funciones.getAsientos(5L)).allMatch(a -> !a.ocupado());
        assertThat(funciones.getAsientos(6L)).allMatch(a -> !a.ocupado());
        assertThat(jdbc.queryForObject("""
            SELECT COUNT(*) FROM reservas r WHERE r.total <>
            (SELECT SUM(e.precio) FROM entradas e WHERE e.reserva_id = r.id)
            """, Integer.class)).isZero();
        assertThat(jdbc.queryForObject("""
            SELECT COUNT(*) FROM entradas e JOIN reservas r ON r.id=e.reserva_id
            JOIN funciones f ON f.id=e.funcion_id JOIN asientos a ON a.id=e.asiento_id
            WHERE e.funcion_id<>r.funcion_id OR a.sala_id<>f.sala_id OR e.precio<>f.precio
            """, Integer.class)).isZero();
        assertThat(jdbc.queryForObject("""
            SELECT COUNT(*) FROM (
              SELECT e.funcion_id,e.asiento_id FROM entradas e
              JOIN reservas r ON r.id=e.reserva_id WHERE r.estado<>'CANCELADA'
              GROUP BY e.funcion_id,e.asiento_id HAVING COUNT(*)>1
            ) duplicados
            """, Integer.class)).isZero();
        for (long id : new long[]{5,6}) {
            String hash = jdbc.queryForObject("SELECT password_hash FROM usuarios WHERE id=?", String.class, id);
            assertThat(passwords.matches("Cinego123!", hash)).isTrue();
            assertThat(jdbc.queryForObject("SELECT rol FROM usuarios WHERE id=?", String.class, id)).isEqualTo("CLIENTE");
        }
    }
}
