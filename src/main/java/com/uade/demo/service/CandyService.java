package com.uade.demo.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.uade.demo.dto.CandyRequestDTO;
import com.uade.demo.dto.CandyResponseDTO;
import com.uade.demo.exception.ResourceNotFoundException;
import com.uade.demo.model.Candy;
import com.uade.demo.repository.CandyRepository;

@Service
@Transactional
public class CandyService {

    private final CandyRepository candyRepository;

    public CandyService(CandyRepository candyRepository) {
        this.candyRepository = candyRepository;
    }

    @Transactional(readOnly = true)
    public List<CandyResponseDTO> findAll() {
        return candyRepository.findAll().stream()
                .map(CandyResponseDTO::new)
                .toList();
    }

    @Transactional(readOnly = true)
    public CandyResponseDTO findById(Long id) {
        return new CandyResponseDTO(buscarOFallar(id));
    }

    public CandyResponseDTO save(CandyRequestDTO dto) {
        validar(dto);
        Candy candy = new Candy();
        aplicarDatos(candy, dto);
        return new CandyResponseDTO(candyRepository.save(candy));
    }

    public CandyResponseDTO update(Long id, CandyRequestDTO dto) {
        validar(dto);
        Candy existente = buscarOFallar(id);
        aplicarDatos(existente, dto);
        return new CandyResponseDTO(candyRepository.save(existente));
    }

    public void deleteById(Long id) {
        if (!candyRepository.existsById(id)) {
            throw new ResourceNotFoundException("No se encontró el producto de candy con id: " + id);
        }
        candyRepository.deleteById(id);
    }

    public void deleteAll() {
        candyRepository.deleteAll();
    }

    private Candy buscarOFallar(Long id) {
        return candyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No se encontró el producto de candy con id: " + id));
    }

    private void validar(CandyRequestDTO dto) {
        if (dto.getNombre() == null || dto.getNombre().isBlank()) {
            throw new IllegalArgumentException("El nombre del producto de candy es requerido");
        }
        if (dto.getPrecio() == null || dto.getPrecio().signum() <= 0) {
            throw new IllegalArgumentException("El precio debe ser mayor a cero");
        }
    }

    private void aplicarDatos(Candy candy, CandyRequestDTO dto) {
        candy.setNombre(dto.getNombre().trim());
        candy.setDescripcion(dto.getDescripcion());
        candy.setPrecio(dto.getPrecio());
        candy.setImagenUrl(dto.getImagenUrl());
    }
}
