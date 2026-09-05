package com.uade.demo.service;

import com.uade.demo.repository.UsuarioRepository;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.uade.demo.model.Reservas;
import com.uade.demo.model.Usuarios;
import com.uade.demo.repository.ReservaRepository;

@Service 
public class ReservaService {
    private final UsuarioRepository usuarioRepository;
    private ReservaRepository reservaRepository;

    public ReservaService( ReservaRepository reservaRepository, UsuarioRepository usuarioRepository){
        this.reservaRepository = reservaRepository;
        this.usuarioRepository = usuarioRepository;
    }

    public Optional<Reservas> getReservaByid(Long id) { 
           return reservaRepository.findById(id);
    }

    public Reservas createReserva(Long usuarioId, Long funcionId, List<Long> asientoId){
        Reservas res = new Reservas();
        Usuarios usuario = usuarioRepository.findById(usuarioId).orElseThrow(() -> new RuntimeException("Usuario not found with id: " + usuarioId));
        res.setUsuario(usuario);
        reservaRepository.save(res);
        return res;

    }

    public void deleteReservas(Long id) {
        reservaRepository.deleteById(id);
    }
    
}
