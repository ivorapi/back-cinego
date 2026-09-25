package com.uade.demo.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.uade.demo.dto.ReservaRequestDTO;
import com.uade.demo.dto.ReservaResponseDTO;
import com.uade.demo.exception.AsientoOcupadoException;
import com.uade.demo.model.EstadoReserva;
import com.uade.demo.service.ReservaService;

@WebMvcTest(ReservaController.class)
@AutoConfigureMockMvc(addFilters = false)
class ReservaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ReservaService reservaService;

    @Test
    void createReserva_devuelve201YLaReservaCreada() throws Exception {
        when(reservaService.createReserva(any(ReservaRequestDTO.class))).thenReturn(respuestaDePrueba());

        mockMvc.perform(post("/api/reservas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"funcionId": 1, "asientoIds": [10, 11]}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.idReserva").value(50))
                .andExpect(jsonPath("$.total").value(16000));

        verify(reservaService).createReserva(any(ReservaRequestDTO.class));
    }

    @Test
    void createReserva_conAsientosVaciosDevuelve400SinLlamarAlServicio() throws Exception {
        mockMvc.perform(post("/api/reservas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"funcionId": 1, "asientoIds": []}
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getMisReservas_devuelveLasReservasDelUsuarioAutenticado() throws Exception {
        when(reservaService.getMisReservas()).thenReturn(List.of(respuestaDePrueba()));

        mockMvc.perform(get("/api/reservas/mis-reservas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].idReserva").value(50));
    }

    @Test
    void getReservaById_devuelve200YLaReserva() throws Exception {
        when(reservaService.getReservaById(50L)).thenReturn(respuestaDePrueba());

        mockMvc.perform(get("/api/reservas/50"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idReserva").value(50));
    }

    @Test
    void cancelarReserva_devuelveLaReservaCancelada() throws Exception {
        ReservaResponseDTO cancelada = respuestaDePrueba();
        cancelada.setEstado(EstadoReserva.CANCELADA);
        when(reservaService.cancelarReserva(50L)).thenReturn(cancelada);

        mockMvc.perform(patch("/api/reservas/50/cancelar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("CANCELADA"));
    }

    @Test
    void createReserva_conAsientoOcupadoDevuelve409() throws Exception {
        when(reservaService.createReserva(any(ReservaRequestDTO.class)))
                .thenThrow(new AsientoOcupadoException("El asiento ya está ocupado"));

        mockMvc.perform(post("/api/reservas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"funcionId": 1, "asientoIds": [10]}
                                """))
                .andExpect(status().isConflict());
    }

    private ReservaResponseDTO respuestaDePrueba() {
        ReservaResponseDTO respuesta = new ReservaResponseDTO();
        respuesta.setIdReserva(50L);
        respuesta.setIdFuncion(1L);
        respuesta.setEstado(EstadoReserva.PENDIENTE);
        respuesta.setFechaReserva(LocalDateTime.of(2026, 10, 1, 20, 0));
        respuesta.setIdsEntradas(List.of(100L, 101L));
        respuesta.setTotal(new BigDecimal("16000.00"));
        return respuesta;
    }
}
