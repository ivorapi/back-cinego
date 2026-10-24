package com.uade.demo.controller;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.net.URI;
import java.net.http.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import com.uade.demo.model.*;
import com.uade.demo.repository.*;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = {
    "spring.datasource.url=jdbc:h2:mem:reservas-http;MODE=MySQL;DB_CLOSE_DELAY=-1",
    "spring.datasource.driver-class-name=org.h2.Driver", "spring.datasource.username=sa",
    "spring.datasource.password=", "spring.jpa.hibernate.ddl-auto=create-drop", "spring.jpa.show-sql=false"
})
class ReservasHttpTest {
    @LocalServerPort int port;
    @Autowired SalaRepository salas;
    @Autowired PeliculaRepository peliculas;
    @Autowired FuncionRepository funciones;
    @Autowired AsientoRepository asientos;
    final HttpClient http = HttpClient.newHttpClient();
    final JsonMapper json = new JsonMapper();

    HttpResponse<String> call(String method, String path, String body, String token) throws Exception {
        var request = HttpRequest.newBuilder(URI.create("http://localhost:" + port + path))
            .header("Content-Type", "application/json");
        if (token != null) request.header("Authorization", "Bearer " + token);
        return http.send(request.method(method, HttpRequest.BodyPublishers.ofString(body)).build(),
            HttpResponse.BodyHandlers.ofString());
    }
    JsonNode body(HttpResponse<String> response, int status) {
        assertThat(response.statusCode()).as(response.body()).isEqualTo(status);
        return json.readTree(response.body());
    }

    @Test
    void flujoCompletoYConcurrenciaPorHttp() throws Exception {
        Salas sala = new Salas(); sala.setNombre("HTTP test"); sala = salas.save(sala);
        Peliculas pelicula = new Peliculas(); pelicula.setTitulo("HTTP test");
        pelicula.setDuracion(120); pelicula.setClasificacion(13); pelicula = peliculas.save(pelicula);
        Funcion funcion = new Funcion(); funcion.setSala(sala); funcion.setPelicula(pelicula);
        funcion.setHorarioInicio(LocalDateTime.now().plusDays(1)); funcion.setPrecio(new BigDecimal("8000"));
        funcion = funciones.save(funcion);
        Funcion otra = new Funcion(); otra.setSala(sala); otra.setPelicula(pelicula);
        otra.setHorarioInicio(LocalDateTime.now().plusDays(2)); otra.setPrecio(new BigDecimal("8000"));
        otra = funciones.save(otra);
        Asiento a = new Asiento(); a.setSala(sala); a.setFila("A"); a.setNumero(1); a = asientos.save(a);
        Asiento b = new Asiento(); b.setSala(sala); b.setFila("A"); b.setNumero(2); b = asientos.save(b);
        String path = "/api/funciones/" + funcion.getId() + "/asientos";
        var libres = body(call("GET", path, "", null), 200);
        assertThat(libres.size()).isEqualTo(2);
        assertThat(libres.get(0).properties()).hasSize(4);
        assertThat(libres.get(0).get("ocupado").asBoolean()).isFalse();
        assertThat(call("GET", "/api/funciones/999999/asientos", "", null).statusCode()).isEqualTo(404);
        String usuario = "{\"email\":\"http@cinego.test\",\"password\":\"test-pass-123\",\"nombre\":\"Http\",\"apellido\":\"Test\"}";
        assertThat(call("POST", "/api/usuarios/registro", usuario, null).statusCode()).isEqualTo(201);
        var login = call("POST", "/api/usuarios/login", usuario, null);
        assertThat(login.statusCode()).isEqualTo(200); String token = login.body();
        String request = "{\"funcionId\":" + funcion.getId() + ",\"asientoIds\":[" + a.getId() + "]}";
        assertThat(call("POST", "/api/reservas", request, null).statusCode()).isIn(401, 403);
        var creada = body(call("POST", "/api/reservas", request, token), 201);
        long id = creada.get("idReserva").asLong();
        assertThat(creada.get("asientos").get(0).get("fila").asText()).isEqualTo("A");
        assertThat(creada.get("idsEntradas").size()).isEqualTo(1);
        assertThat(body(call("GET", path, "", null), 200).get(0).get("ocupado").asBoolean()).isTrue();
        assertThat(body(call("GET", "/api/funciones/" + otra.getId() + "/asientos", "", null), 200)
            .get(0).get("ocupado").asBoolean()).isFalse();
        assertThat(call("POST", "/api/reservas", request, token).statusCode()).isEqualTo(409);
        body(call("GET", "/api/reservas/" + id, "", token), 200);
        assertThat(body(call("GET", "/api/reservas/mis-reservas", "", token), 200).size()).isEqualTo(1);
        assertThat(body(call("PATCH", "/api/reservas/" + id + "/cancelar", "", token), 200)
            .get("estado").asText()).isEqualTo("CANCELADA");
        assertThat(body(call("GET", path, "", null), 200).get(0).get("ocupado").asBoolean()).isFalse();
        assertThat(call("PATCH", "/api/reservas/" + id + "/cancelar", "", token).statusCode()).isEqualTo(400);
        assertThat(call("POST", "/api/reservas", "{\"funcionId\":" + funcion.getId() + ",\"asientoIds\":[]}", token)
            .statusCode()).isEqualTo(400);
        var first = CompletableFuture.supplyAsync(() -> reserve(request, token));
        var second = CompletableFuture.supplyAsync(() -> reserve(request, token));
        assertThat(List.of(first.get(), second.get())).containsExactlyInAnyOrder(201, 409);
    }

    int reserve(String request, String token) {
        try { return call("POST", "/api/reservas", request, token).statusCode(); }
        catch (Exception ex) { throw new RuntimeException(ex); }
    }
}
