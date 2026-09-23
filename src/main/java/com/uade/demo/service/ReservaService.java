package com.uade.demo.service;

import com.uade.demo.repository.UsuarioRepository;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.uade.demo.model.Reservas;
import com.uade.demo.model.Usuarios;
import com.uade.demo.model.Funcion;
import com.uade.demo.repository.ReservaRepository;
import com.uade.demo.repository.FuncionRepository;

@Service 
public class ReservaService {
    private final UsuarioRepository usuarioRepository;
    private final FuncionRepository funcionRepository;
    private ReservaRepository reservaRepository;

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
        Reservas res = new Reservas();
        Usuarios usuario = usuarioRepository.findById(usuarioId).orElseThrow(() -> new RuntimeException("Usuario not found with id: " + usuarioId));
        Funcion funcion = funcionRepository.findById(funcionId).orElseThrow(() -> new RuntimeException("Funcion not found with id: " + funcionId));
        res.setUsuario(usuario);
        res.setFuncion(funcion);
        reservaRepository.save(res);
        return res;

    }

    public void deleteReservas(Long id) {
        reservaRepository.deleteById(id);
    }
    
}
