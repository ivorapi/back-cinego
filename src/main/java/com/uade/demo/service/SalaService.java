package com.uade.demo.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.uade.demo.model.Salas;
import com.uade.demo.repository.SalaRepository;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class SalaService {

    private final SalaRepository salaRepository;

    public SalaService(SalaRepository salaRepository) {
        this.salaRepository = salaRepository;
    }

    public List<Salas> findAll() {
        return salaRepository.findAll();
    }

    public Salas findById(Long id) {
        return salaRepository.findById(id).orElse(null);
    }

    public Salas save(Salas sala) {
        return salaRepository.save(sala);
    }

    public void deleteById(Long id) {
        salaRepository.deleteById(id);
    }

    public void deleteAll() {
        salaRepository.deleteAll();
    }
}