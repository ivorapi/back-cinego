package com.uade.demo.service;

import com.uade.demo.repository.EntradaRepository;
import com.uade.demo.repository.UsuarioRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.uade.demo.dto.ReservaRequestDTO;
import com.uade.demo.dto.ReservaResponseDTO;
import com.uade.demo.exception.ResourceNotFoundException;
import com.uade.demo.model.Asiento;
import com.uade.demo.model.Entrada;
import com.uade.demo.model.EstadoReserva;
import com.uade.demo.model.Funcion;
import com.uade.demo.model.Reserva;
import com.uade.demo.model.Usuarios;
import com.uade.demo.repository.AsientoRepository;
import com.uade.demo.repository.FuncionRepository;
import com.uade.demo.repository.ReservaRepository;

@Service 
public class ReservaService {
    private final EntradaRepository entradaRepository;
    private final UsuarioRepository usuarioRepository;
    private ReservaRepository reservaRepository;
    private final FuncionRepository funcionRepository;
    private final AsientoRepository asientoRepository;

    public ReservaService( ReservaRepository reservaRepository, UsuarioRepository usuarioRepository, FuncionRepository funcionRepository, AsientoRepository asientoRepository, EntradaRepository entradaRepository) {
        this.reservaRepository = reservaRepository;
        this.usuarioRepository = usuarioRepository;
        this.funcionRepository = funcionRepository;
        this.asientoRepository = asientoRepository;
        this.entradaRepository = entradaRepository;


    }

    public Optional<Reserva> getReservaByid(Long id) { 
           return reservaRepository.findById(id);
    }

    public List<Reserva> getReservasDelUsuarioAutenticado() {
        Usuarios usuario = obtenerUsuarioAutenticado();
        return reservaRepository.findByUsuarioId(usuario.getId());
    }

    @Transactional 
    public ReservaResponseDTO createReserva(ReservaRequestDTO reservaRequest) {
        Reserva reserva = new Reserva();
        Usuarios usuario = obtenerUsuarioAutenticado();
        reserva.setUsuario(usuario);

        Funcion funcion = funcionRepository.findById(reservaRequest.getFuncionId())
                .orElseThrow(() -> new ResourceNotFoundException("Función no encontrada con id: " + reservaRequest.getFuncionId()));
        reserva.setFuncion(funcion);
        
        List<Long> asientoIds = reservaRequest.getAsientoId();
        List<Asiento> asientos = obtenerAsientosPorIds(asientoIds, funcion.getSala().getId());

        Reserva reservaGuardada = reservaRepository.save(reserva);
        
        List<Entrada> entradas = asientos.stream()
            .map(asiento -> crearEntrada(reservaGuardada, funcion, asiento))
            .toList();

            entradaRepository.saveAll(entradas);
            
            reservaGuardada.getEntradas().addAll(entradas);

            BigDecimal total = entradas.stream()
                .map(Entrada::getPrecio)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

            reservaGuardada.setTotal(total);

            ReservaResponseDTO respuesta = new ReservaResponseDTO();
            respuesta.setReserva(reservaGuardada.getId());
            respuesta.setFuncion(funcion.getId());
            respuesta.setEstado(reservaGuardada.getEstado());
            respuesta.setFechaReserva(reservaGuardada.getCreadaEn());
            respuesta.setEntradas(entradas.stream().map(Entrada::getId).toList());
            respuesta.setTotal(total);

            return respuesta;

    }

    public void deleteReserva(Long id) {
        reservaRepository.deleteById(id);
    }

    public Reserva cancelarReserva(Long id) {
        Reserva reserva = reservaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reserva no encontrada con id: " + id));
        reserva.setEstado(EstadoReserva.CANCELADA);
        return reservaRepository.save(reserva);
    }
    
    private Usuarios obtenerUsuarioAutenticado() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    if (authentication == null
            || !authentication.isAuthenticated()
            || "anonymousUser".equals(authentication.getPrincipal())) {
        throw new IllegalArgumentException("Debés iniciar sesión para crear una reserva");
    }

    String email = authentication.getName();

    return usuarioRepository.findByEmail(email)
            .orElseThrow(() -> new ResourceNotFoundException(
                    "No se encontró el usuario autenticado"));
}

private List<Asiento> obtenerAsientosPorIds(List<Long> asientoIds, Long salaFuncionId) {
    List<Asiento> asientos = asientoRepository.findAllById(asientoIds);

    if (asientos.size() != asientoIds.size()) {
        throw new ResourceNotFoundException("Uno o más asientos no existen");
    }


        boolean hayAsientoDeOtraSala = asientos.stream()
                .anyMatch(asiento -> !asiento.getSala().getId().equals(salaFuncionId));

        if (hayAsientoDeOtraSala) {
            throw new IllegalArgumentException(
                    "Todos los asientos deben pertenecer a la sala de la función");
        }


    return asientos;
}

private Entrada crearEntrada(
        Reserva reserva,
        Funcion funcion,
        Asiento asiento) {

    Entrada entrada = new Entrada();
    entrada.setReserva(reserva);
    entrada.setFuncion(funcion);
    entrada.setAsiento(asiento);
    entrada.setPrecio(funcion.getPrecio());
    entrada.setCodigo("CNG-" + UUID.randomUUID());

    return entrada;
}
}
