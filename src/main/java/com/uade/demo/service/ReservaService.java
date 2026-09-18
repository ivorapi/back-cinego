package com.uade.demo.service;

import com.uade.demo.repository.EntradaRepository;
import com.uade.demo.repository.UsuarioRepository;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.uade.demo.dto.ReservaRequestDTO;
import com.uade.demo.dto.ReservaResponseDTO;
import com.uade.demo.exception.AsientoOcupadoException;
import com.uade.demo.exception.ResourceNotFoundException;
import com.uade.demo.model.Asiento;
import com.uade.demo.model.Entrada;
import com.uade.demo.model.EstadoReserva;
import com.uade.demo.model.Funcion;
import com.uade.demo.model.Reserva;
import com.uade.demo.model.Rol;
import com.uade.demo.model.Usuarios;
import com.uade.demo.repository.AsientoRepository;
import com.uade.demo.repository.FuncionRepository;
import com.uade.demo.repository.ReservaRepository;

@Service 
public class ReservaService {
    private final EntradaRepository entradaRepository;
    private final UsuarioRepository usuarioRepository;
    private final ReservaRepository reservaRepository;
    private final FuncionRepository funcionRepository;
    private final AsientoRepository asientoRepository;

    public ReservaService( ReservaRepository reservaRepository, UsuarioRepository usuarioRepository, FuncionRepository funcionRepository, AsientoRepository asientoRepository, EntradaRepository entradaRepository) {
        this.reservaRepository = reservaRepository;
        this.usuarioRepository = usuarioRepository;
        this.funcionRepository = funcionRepository;
        this.asientoRepository = asientoRepository;
        this.entradaRepository = entradaRepository;


    }

    @Transactional(readOnly = true)
    public ReservaResponseDTO getReservaById(Long id) {
        Reserva reserva = buscarReserva(id);
        validarAccesoAReserva(reserva);
        return toResponseDTO(reserva);
    }

    @Transactional(readOnly = true)
    public List<ReservaResponseDTO> getMisReservas() {
        Usuarios usuario = obtenerUsuarioAutenticado();
        return reservaRepository.findByUsuario_Id(usuario.getId()).stream()
                .map(this::toResponseDTO)
                .toList();
    }

    @Transactional 
    public ReservaResponseDTO createReserva(ReservaRequestDTO reservaRequest) {
        Reserva reserva = new Reserva();
        Usuarios usuario = obtenerUsuarioAutenticado();
        reserva.setUsuario(usuario);

        Funcion funcion = funcionRepository.findById(reservaRequest.getFuncionId())
                .orElseThrow(() -> new ResourceNotFoundException("Función no encontrada con id: " + reservaRequest.getFuncionId()));
        reserva.setFuncion(funcion);
        
        List<Long> asientoIds = reservaRequest.getAsientoIds();
        validarIdsDeAsientosSinRepetidos(asientoIds);
        List<Asiento> asientos = obtenerAsientosPorIds(asientoIds, funcion.getSala().getId());
        validarDisponibilidad(funcion, asientos);
        
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

        return toResponseDTO(reservaGuardada);

    }

    @Transactional
    public ReservaResponseDTO cancelarReserva(Long id) {
        Reserva reserva = buscarReserva(id);
        validarAccesoAReserva(reserva);

        if (reserva.getEstado() == EstadoReserva.CANCELADA) {
            throw new IllegalArgumentException("La reserva ya está cancelada");
        }

        reserva.setEstado(EstadoReserva.CANCELADA);
        return toResponseDTO(reserva);
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

private void validarIdsDeAsientosSinRepetidos(List<Long> asientoIds) {
    Set<Long> idsUnicos = new HashSet<>(asientoIds);
    if (idsUnicos.size() != asientoIds.size()) {
        throw new IllegalArgumentException("No podés repetir asientos en una misma reserva");
    }
}

private void validarDisponibilidad(Funcion funcion, List<Asiento> asientos) {
    boolean hayAsientoOcupado = asientos.stream()
            .anyMatch(asiento -> entradaRepository.existeEntradaActiva(
                    funcion.getId(),
                    asiento.getId(),
                    EstadoReserva.CANCELADA));

    if (hayAsientoOcupado) {
        throw new AsientoOcupadoException(
                "Uno o más asientos ya están reservados para esta función");
    }
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

private Reserva buscarReserva(Long id) {
    return reservaRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Reserva no encontrada con id: " + id));
}

private void validarAccesoAReserva(Reserva reserva) {
    Usuarios usuario = obtenerUsuarioAutenticado();
    boolean esPropietario = reserva.getUsuarioId().equals(usuario.getId());
    boolean esAdministrador = usuario.getRol() == Rol.ADMIN || usuario.getRol() == Rol.SUPER_ADMIN;

    if (!esPropietario && !esAdministrador) {
        throw new AccessDeniedException("No tenés permisos para acceder a esta reserva");
    }
}

private ReservaResponseDTO toResponseDTO(Reserva reserva) {
    ReservaResponseDTO respuesta = new ReservaResponseDTO();
    respuesta.setIdReserva(reserva.getId());
    respuesta.setIdFuncion(reserva.getFuncionId());
    respuesta.setEstado(reserva.getEstado());
    respuesta.setFechaReserva(reserva.getCreadaEn());
    respuesta.setTotal(reserva.getTotal());
    respuesta.setIdsEntradas(entradaRepository.findByReserva_Id(reserva.getId()).stream()
            .map(Entrada::getId)
            .toList());
    return respuesta;
}
}
