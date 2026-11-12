package com.uade.demo.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.uade.demo.dto.SalaRequestDTO;
import com.uade.demo.dto.SalaResponseDTO;
import com.uade.demo.exception.ResourceNotFoundException;
import com.uade.demo.model.Salas;
import com.uade.demo.repository.AsientoRepository;
import com.uade.demo.repository.SalaRepository;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class SalaService {

    private static final int MAX_FILAS = 30;
    private static final int MAX_ASIENTOS_POR_FILA = 40;

    private final SalaRepository salaRepository;
    private final AsientoRepository asientoRepository;

    public SalaService(SalaRepository salaRepository, AsientoRepository asientoRepository) {
        this.salaRepository = salaRepository;
        this.asientoRepository = asientoRepository;
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
        aplicarDatos(sala, dto);
        return new SalaResponseDTO(salaRepository.save(sala));
    }

    public SalaResponseDTO update(Long id, SalaRequestDTO dto) {
        validar(dto);
        Salas existente = buscarOFallar(id);
        aplicarDatos(existente, dto);
        return new SalaResponseDTO(salaRepository.save(existente));
    }

    public void deleteById(Long id) {
        Salas sala = buscarOFallar(id);
        int funciones = sala.getFunciones().size();
        if (funciones > 0) {
            throw new IllegalArgumentException("No se puede eliminar la sala: tiene " + funciones
                    + (funciones == 1 ? " función programada." : " funciones programadas."));
        }
        asientoRepository.deleteAll(sala.getAsientos());
        salaRepository.delete(sala);
    }

    public void deleteAll() {
        salaRepository.deleteAll();
    }

    private Salas buscarOFallar(Long id) {
        return salaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró la sala con id: " + id));
    }

    private void aplicarDatos(Salas sala, SalaRequestDTO dto) {
        sala.setNombre(dto.getNombre().trim());
        sala.setFilas(dto.getFilas());
        sala.setAsientosPorFila(dto.getAsientosPorFila());
    }

    private void validar(SalaRequestDTO dto) {
        if (dto.getNombre() == null || dto.getNombre().isBlank()) {
            throw new IllegalArgumentException("El nombre de la sala es requerido");
        }
        validarDistribucion(dto.getFilas(), dto.getAsientosPorFila());
    }

    private void validarDistribucion(Integer filas, Integer asientosPorFila) {
        if (filas == null || filas < 1 || filas > MAX_FILAS) {
            throw new IllegalArgumentException("La sala tiene que tener entre 1 y " + MAX_FILAS + " filas");
        }
        if (asientosPorFila == null || asientosPorFila < 1 || asientosPorFila > MAX_ASIENTOS_POR_FILA) {
            throw new IllegalArgumentException(
                    "Cada fila tiene que tener entre 1 y " + MAX_ASIENTOS_POR_FILA + " asientos");
        }
    }
}
