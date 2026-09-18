package com.uade.demo.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.uade.demo.model.Funcion;
import com.uade.demo.model.Reservas;
import com.uade.demo.model.Usuarios;
import com.uade.demo.repository.FuncionRepository;
import com.uade.demo.repository.ReservaRepository;
import com.uade.demo.repository.UsuarioRepository;

@ExtendWith(MockitoExtension.class)
class ReservaServiceTest {

    @Mock
    private ReservaRepository reservaRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private FuncionRepository funcionRepository;

    @InjectMocks
    private ReservaService reservaService;

    @Test
    void createReservaDebeGuardarUsuarioYFuncionSinAsientos() {
        Usuarios usuario = new Usuarios();
        usuario.setId(1L);
        usuario.setEmail("superadmin@cinego.com");

        Funcion funcion = new Funcion();
        funcion.setId(2L);
        funcion.setHorarioInicio(LocalDateTime.now());
        funcion.setPrecio(new BigDecimal("2500"));

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(funcionRepository.findById(2L)).thenReturn(Optional.of(funcion));
        when(reservaRepository.save(any(Reservas.class))).thenAnswer(invocation -> {
            Reservas reservaGuardada = invocation.getArgument(0);
            reservaGuardada.setId(10L);
            return reservaGuardada;
        });

        Reservas reserva = reservaService.createReserva(1L, 2L);

        assertNotNull(reserva);
        assertEquals(usuario.getId(), reserva.getUsuario().getId());
        assertEquals(funcion.getId(), reserva.getFuncion().getId());
        verify(reservaRepository).save(any(Reservas.class));
    }
}
