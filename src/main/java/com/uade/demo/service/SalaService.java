package com.uade.demo.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.uade.demo.dto.SalaRequestDTO;
import com.uade.demo.dto.SalaResponseDTO;
import com.uade.demo.exception.ResourceNotFoundException;
import com.uade.demo.model.Asiento;
import com.uade.demo.model.Salas;
import com.uade.demo.repository.AsientoRepository;
import com.uade.demo.repository.EntradaRepository;
import com.uade.demo.repository.SalaRepository;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class SalaService {

    private static final int MAX_FILAS = 30;
    private static final int MAX_ASIENTOS_POR_FILA = 40;

    private final SalaRepository salaRepository;
    private final AsientoRepository asientoRepository;
    private final EntradaRepository entradaRepository;

    public SalaService(SalaRepository salaRepository, AsientoRepository asientoRepository,
            EntradaRepository entradaRepository) {
        this.salaRepository = salaRepository;
        this.asientoRepository = asientoRepository;
        this.entradaRepository = entradaRepository;
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
        validarDistribucion(dto.getFilas(), dto.getAsientosPorFila());
        Salas sala = new Salas();
        sala.setNombre(dto.getNombre());
        Salas guardada = salaRepository.save(sala);
        guardada.setAsientos(generarAsientos(guardada, dto.getFilas(), dto.getAsientosPorFila()));
        return new SalaResponseDTO(guardada);
    }

    public SalaResponseDTO update(Long id, SalaRequestDTO dto) {
        validar(dto);
        Salas existente = buscarOFallar(id);
        existente.setNombre(dto.getNombre());

        // Si no mandan la distribución, los asientos quedan como están
        if (dto.getFilas() != null || dto.getAsientosPorFila() != null) {
            validarDistribucion(dto.getFilas(), dto.getAsientosPorFila());
            SalaResponseDTO actual = new SalaResponseDTO(existente);
            boolean cambio = actual.getFilas() != dto.getFilas()
                    || actual.getAsientosPorFila() != dto.getAsientosPorFila();

            if (cambio) {
                if (entradaRepository.existsByAsiento_Sala_Id(id)) {
                    throw new IllegalArgumentException(
                            "No se puede cambiar la cantidad de asientos: la sala ya tiene entradas vendidas.");
                }
                asientoRepository.deleteAll(existente.getAsientos());
                existente.setAsientos(generarAsientos(existente, dto.getFilas(), dto.getAsientosPorFila()));
            }
        }

        return new SalaResponseDTO(salaRepository.save(existente));
    }

    public void deleteById(Long id) {
        Salas sala = buscarOFallar(id);
        int funciones = sala.getFunciones().size();
        if (funciones > 0) {
            throw new IllegalArgumentException("No se puede eliminar la sala: tiene " + funciones
                    + (funciones == 1 ? " función programada." : " funciones programadas."));
        }
        // Primero los asientos, porque apuntan a la sala
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

    private void validar(SalaRequestDTO dto) {
        if (dto.getNombre() == null || dto.getNombre().isBlank()) {
            throw new IllegalArgumentException("El nombre de la sala es requerido");
        }
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

    // Crea los asientos A1, A2... de cada fila
    private List<Asiento> generarAsientos(Salas sala, int filas, int asientosPorFila) {
        List<Asiento> asientos = new ArrayList<>();
        for (int f = 0; f < filas; f++) {
            String fila = nombreFila(f);
            for (int numero = 1; numero <= asientosPorFila; numero++) {
                Asiento asiento = new Asiento();
                asiento.setSala(sala);
                asiento.setFila(fila);
                asiento.setNumero(numero);
                asientos.add(asiento);
            }
        }
        return new ArrayList<>(asientoRepository.saveAll(asientos));
    }

    // 0 -> "A", 25 -> "Z", 26 -> "AA" (hacen falta dos letras a partir de la fila 27)
    private String nombreFila(int indice) {
        String nombre = "";
        int resto = indice;
        do {
            nombre = (char) ('A' + resto % 26) + nombre;
            resto = resto / 26 - 1;
        } while (resto >= 0);
        return nombre;
    }
}
