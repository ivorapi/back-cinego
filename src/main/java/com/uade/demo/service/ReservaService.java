package com.uade.demo.service;

import java.util.List;
import java.util.Optional;

import com.uade.demo.model.Reservas;
import com.uade.demo.repository.ReservaRepository;

public class ReservaService {
    private ReservaRepository reservaRepository;

    public ReservaService( ReservaRepository reservaRepository){
        this.reservaRepository = reservaRepository;
    }

    public Optional<Reservas> getReservaByid(Long id) { 
           return reservaRepository.findById(id);
    }

    public Reservas createReserva(Long usuarioId, Long funcionId, List<Long> asientoId){
        
        return null;

    }
    
}
