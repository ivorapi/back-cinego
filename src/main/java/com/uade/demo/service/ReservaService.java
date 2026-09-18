package com.uade.demo.service;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.uade.demo.exception.ResourceNotFoundException;
import com.uade.demo.model.Funcion;
import com.uade.demo.model.Reservas;
import com.uade.demo.model.Usuarios;
import com.uade.demo.repository.FuncionRepository;
import com.uade.demo.repository.ReservaRepository;
import com.uade.demo.repository.UsuarioRepository;

@Service
public class ReservaService {

    private final ReservaRepository reservaRepository;
    private final UsuarioRepository usuarioRepository;
    private final FuncionRepository funcionRepository;

    public ReservaService(ReservaRepository reservaRepository, UsuarioRepository usuarioRepository,
            FuncionRepository funcionRepository) {
        this.reservaRepository = reservaRepository;
        this.usuarioRepository = usuarioRepository;
        this.funcionRepository = funcionRepository;
    }

    public Optional<Reservas> getReservaByid(Long id) {
        return reservaRepository.findById(id);
    }

    public Reservas createReserva(Long usuarioId, Long funcionId) {
        if (usuarioId == null || funcionId == null) {
            throw new IllegalArgumentException("usuarioId y funcionId son requeridos");
        }

        Usuarios usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró el usuario con id: " + usuarioId));

        Funcion funcion = funcionRepository.findById(funcionId)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró la función con id: " + funcionId));

        Reservas reserva = new Reservas();
        reserva.setUsuario(usuario);
        reserva.setFuncion(funcion);

        return reservaRepository.save(reserva);
    }

    public void deleteReservas(Long id) {
        reservaRepository.deleteById(id);
    }
}
