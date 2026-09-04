package com.uade.demo.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.uade.demo.dto.SalaRequestDTO;
import com.uade.demo.dto.SalaResponseDTO;
import com.uade.demo.exception.ResourceNotFoundException;
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

    public List<SalaResponseDTO> findAll() {
        return salaRepository.findAll().stream()
                .map(SalaResponseDTO::new)
                .toList();
    }

    public SalaResponseDTO findById(Long id) {
        return new SalaResponseDTO(buscarOFallar(id));
    }

    public SalaResponseDTO save(SalaRequestDTO dto) {
        validar(dto);
        Salas sala = new Salas();
        sala.setNombre(dto.getNombre());
        return new SalaResponseDTO(salaRepository.save(sala));
    }

    public SalaResponseDTO update(Long id, SalaRequestDTO dto) {
        validar(dto);
        Salas existente = buscarOFallar(id);
        existente.setNombre(dto.getNombre());
        return new SalaResponseDTO(salaRepository.save(existente));
    }

    public void deleteById(Long id) {
        salaRepository.deleteById(id);
    }

    public void deleteAll() {
        salaRepository.deleteAll();
    }

    private Salas buscarOFallar(Long id) {
        return salaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró la sala con id: " + id));
    }

    private void validar(SalaRequestDTO dto) {
        if (dto.getNombre() == null || dto.getNombre().isBlank()) {
            throw new IllegalArgumentException("El nombre de la sala es requerido");
        }
    }
}