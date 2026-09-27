package com.uade.demo.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import com.uade.demo.dto.ReservaRequestDTO;
import com.uade.demo.exception.AsientoOcupadoException;
import com.uade.demo.exception.ResourceNotFoundException;
import com.uade.demo.model.Asiento;
import com.uade.demo.model.Entrada;
import com.uade.demo.model.EstadoReserva;
import com.uade.demo.model.Funcion;
import com.uade.demo.model.Peliculas;
import com.uade.demo.model.Reserva;
import com.uade.demo.model.Rol;
import com.uade.demo.model.Salas;
import com.uade.demo.model.Usuarios;
import com.uade.demo.repository.AsientoRepository;
import com.uade.demo.repository.EntradaRepository;
import com.uade.demo.repository.FuncionRepository;
import com.uade.demo.repository.ReservaRepository;
import com.uade.demo.repository.UsuarioRepository;

@ExtendWith(MockitoExtension.class)
class ReservaServiceTest {

    @Mock private ReservaRepository reservaRepository;
    @Mock private UsuarioRepository usuarioRepository;
    @Mock private FuncionRepository funcionRepository;
    @Mock private AsientoRepository asientoRepository;
    @Mock private EntradaRepository entradaRepository;
    @Captor private ArgumentCaptor<List<Entrada>> entradasCaptor;

    @InjectMocks private ReservaService reservaService;

    @AfterEach
    void limpiarContextoDeSeguridad() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void createReserva_creaUnaEntradaPorCadaAsientoYCalculaElTotal() {
        Usuarios cliente = clienteAutenticado();
        Funcion funcion = funcionDePrueba();
        List<Asiento> asientos = List.of(asiento(10L, funcion.getSala()), asiento(11L, funcion.getSala()));
        ReservaRequestDTO request = request(1L, List.of(10L, 11L));

        when(usuarioRepository.findByEmail(cliente.getEmail())).thenReturn(java.util.Optional.of(cliente));
        when(funcionRepository.findById(1L)).thenReturn(java.util.Optional.of(funcion));
        when(asientoRepository.findAllById(request.getAsientoIds())).thenReturn(asientos);
        when(entradaRepository.existeEntradaActiva(anyLong(), anyLong(), any())).thenReturn(false);
        when(reservaRepository.save(any(Reserva.class))).thenAnswer(invocation -> {
            Reserva reserva = invocation.getArgument(0);
            reserva.setId(50L);
            return reserva;
        });
        when(entradaRepository.saveAll(entradasCaptor.capture())).thenAnswer(invocation -> {
            List<Entrada> entradas = invocation.getArgument(0);
            entradas.get(0).setId(100L);
            entradas.get(1).setId(101L);
            return entradas;
        });
        when(entradaRepository.findByReserva_Id(50L)).thenAnswer(invocation -> entradasCaptor.getValue());

        var respuesta = reservaService.createReserva(request);

        assertThat(entradasCaptor.getValue()).hasSize(2);
        assertThat(respuesta.getIdReserva()).isEqualTo(50L);
        assertThat(respuesta.getIdsEntradas()).containsExactly(100L, 101L);
        assertThat(respuesta.getTotal()).isEqualByComparingTo("16000.00");
    }

    @Test
    void createReserva_rechazaUnAsientoOcupado() {
        Usuarios cliente = clienteAutenticado();
        Funcion funcion = funcionDePrueba();
        Asiento asiento = asiento(10L, funcion.getSala());
        ReservaRequestDTO request = request(1L, List.of(10L));

        when(usuarioRepository.findByEmail(cliente.getEmail())).thenReturn(java.util.Optional.of(cliente));
        when(funcionRepository.findById(1L)).thenReturn(java.util.Optional.of(funcion));
        when(asientoRepository.findAllById(request.getAsientoIds())).thenReturn(List.of(asiento));
        when(entradaRepository.existeEntradaActiva(eq(1L), eq(10L), any())).thenReturn(true);

        assertThatThrownBy(() -> reservaService.createReserva(request))
                .isInstanceOf(AsientoOcupadoException.class)
                .hasMessageContaining("ya están reservados");
    }

    @Test
    void createReserva_sinUsuarioAutenticadoEsRechazada() {
        ReservaRequestDTO request = request(1L, List.of(10L));

        assertThatThrownBy(() -> reservaService.createReserva(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("iniciar sesión");
    }

    @Test
    void createReserva_conFuncionInexistenteInformaElError() {
        Usuarios cliente = clienteAutenticado();
        ReservaRequestDTO request = request(404L, List.of(10L));
        when(usuarioRepository.findByEmail(cliente.getEmail())).thenReturn(java.util.Optional.of(cliente));
        when(funcionRepository.findById(404L)).thenReturn(java.util.Optional.empty());

        assertThatThrownBy(() -> reservaService.createReserva(request))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Función no encontrada");
    }

    @Test
    void createReserva_conAsientoInexistenteInformaElError() {
        Usuarios cliente = clienteAutenticado();
        Funcion funcion = funcionDePrueba();
        ReservaRequestDTO request = request(1L, List.of(999L));
        when(usuarioRepository.findByEmail(cliente.getEmail())).thenReturn(java.util.Optional.of(cliente));
        when(funcionRepository.findById(1L)).thenReturn(java.util.Optional.of(funcion));
        when(asientoRepository.findAllById(request.getAsientoIds())).thenReturn(List.of());

        assertThatThrownBy(() -> reservaService.createReserva(request))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("asientos no existen");
    }

    @Test
    void createReserva_conAsientosRepetidosEsRechazada() {
        Usuarios cliente = clienteAutenticado();
        Funcion funcion = funcionDePrueba();
        ReservaRequestDTO request = request(1L, List.of(10L, 10L));
        when(usuarioRepository.findByEmail(cliente.getEmail())).thenReturn(java.util.Optional.of(cliente));
        when(funcionRepository.findById(1L)).thenReturn(java.util.Optional.of(funcion));

        assertThatThrownBy(() -> reservaService.createReserva(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("repetir asientos");
    }

    @Test
    void getReservaById_devuelveLaReservaDelClientePropietario() {
        Usuarios cliente = clienteAutenticado();
        Reserva reserva = reservaDePrueba(cliente, funcionDePrueba());
        Entrada entrada = new Entrada();
        entrada.setId(100L);

        when(reservaRepository.findById(50L)).thenReturn(java.util.Optional.of(reserva));
        when(entradaRepository.findByReserva_Id(50L)).thenReturn(List.of(entrada));

        var respuesta = reservaService.getReservaById(50L);

        assertThat(respuesta.getIdReserva()).isEqualTo(50L);
        assertThat(respuesta.getIdsEntradas()).containsExactly(100L);
        assertThat(respuesta.getTotal()).isEqualByComparingTo("8000.00");
    }

    @Test
    void getReservaById_deOtroClienteRechazaElAcceso() {
        Usuarios clienteAutenticado = clienteAutenticado();
        Usuarios propietario = new Usuarios();
        propietario.setId(99L);
        Reserva reserva = reservaDePrueba(propietario, funcionDePrueba());

        when(reservaRepository.findById(50L)).thenReturn(java.util.Optional.of(reserva));

        assertThatThrownBy(() -> reservaService.getReservaById(50L))
                .isInstanceOf(org.springframework.security.access.AccessDeniedException.class);
        assertThat(clienteAutenticado.getId()).isNotEqualTo(propietario.getId());
    }

    @Test
    void getReservaById_deOtroClientePeroAdminPermiteElAcceso() {
        Usuarios admin = clienteAutenticado();
        admin.setRol(Rol.ADMIN);
        Usuarios propietario = new Usuarios();
        propietario.setId(99L);
        Reserva reserva = reservaDePrueba(propietario, funcionDePrueba());

        when(reservaRepository.findById(50L)).thenReturn(java.util.Optional.of(reserva));
        when(entradaRepository.findByReserva_Id(50L)).thenReturn(List.of());

        var respuesta = reservaService.getReservaById(50L);

        assertThat(respuesta.getIdReserva()).isEqualTo(50L);
    }

    @Test
    void getReservaById_sinUsuarioAutenticadoEsRechazada() {
        Reserva reserva = reservaDePrueba(new Usuarios(), funcionDePrueba());
        when(reservaRepository.findById(50L)).thenReturn(java.util.Optional.of(reserva));

        assertThatThrownBy(() -> reservaService.getReservaById(50L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("iniciar sesión");
    }

    @Test
    void cancelarReserva_delPropietarioCambiaSuEstado() {
        Usuarios cliente = clienteAutenticado();
        Reserva reserva = reservaDePrueba(cliente, funcionDePrueba());

        when(reservaRepository.findById(50L)).thenReturn(java.util.Optional.of(reserva));
        when(entradaRepository.findByReserva_Id(50L)).thenReturn(List.of());

        var respuesta = reservaService.cancelarReserva(50L);

        assertThat(reserva.getEstado()).isEqualTo(EstadoReserva.CANCELADA);
        assertThat(respuesta.getEstado()).isEqualTo(EstadoReserva.CANCELADA);
    }

    @Test
    void cancelarReserva_yaCanceladaEsRechazada() {
        Usuarios cliente = clienteAutenticado();
        Reserva reserva = reservaDePrueba(cliente, funcionDePrueba());
        reserva.setEstado(EstadoReserva.CANCELADA);
        when(reservaRepository.findById(50L)).thenReturn(java.util.Optional.of(reserva));

        assertThatThrownBy(() -> reservaService.cancelarReserva(50L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("ya está cancelada");
    }

    @Test
    void getReservaById_inexistenteInformaQueNoExiste() {
        clienteAutenticado();
        when(reservaRepository.findById(404L)).thenReturn(java.util.Optional.empty());

        assertThatThrownBy(() -> reservaService.getReservaById(404L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Reserva no encontrada");
    }

    private Usuarios clienteAutenticado() {
        Usuarios cliente = new Usuarios();
        cliente.setId(1L);
        cliente.setEmail("cliente@cinego.test");
        cliente.setRol(Rol.CLIENTE);
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(cliente.getEmail(), null, List.of()));
        return cliente;
    }

    private Funcion funcionDePrueba() {
        Salas sala = new Salas();
        sala.setId(2L);
        sala.setNombre("Sala 2");
        Peliculas pelicula = new Peliculas();
        pelicula.setTitulo("Película");
        Funcion funcion = new Funcion();
        funcion.setId(1L);
        funcion.setSala(sala);
        funcion.setPelicula(pelicula);
        funcion.setPrecio(new BigDecimal("8000.00"));
        return funcion;
    }

    private Asiento asiento(Long id, Salas sala) {
        Asiento asiento = new Asiento();
        asiento.setId(id);
        asiento.setSala(sala);
        return asiento;
    }

    private Reserva reservaDePrueba(Usuarios usuario, Funcion funcion) {
        Reserva reserva = new Reserva();
        reserva.setId(50L);
        reserva.setUsuario(usuario);
        reserva.setFuncion(funcion);
        reserva.setTotal(new BigDecimal("8000.00"));
        reserva.setCreadaEn(java.time.LocalDateTime.of(2026, 10, 1, 20, 0));
        return reserva;
    }

    private ReservaRequestDTO request(Long funcionId, List<Long> asientoIds) {
        ReservaRequestDTO request = new ReservaRequestDTO();
        request.setFuncionId(funcionId);
        request.setAsientoIds(asientoIds);
        return request;
    }
}
