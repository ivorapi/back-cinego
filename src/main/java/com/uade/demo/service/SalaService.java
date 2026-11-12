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
        Salas sala = new Salas();
        aplicarDatos(sala, dto);
        Salas guardada = salaRepository.save(sala);
        guardada.setAsientos(generarAsientos(guardada, dto.getFilas(), dto.getAsientosPorFila()));
        return new SalaResponseDTO(guardada);
    }

    public SalaResponseDTO update(Long id, SalaRequestDTO dto) {
        validar(dto);
        Salas existente = buscarOFallar(id);

        boolean cambioGrilla = existente.getFilas() != dto.getFilas()
                || existente.getAsientosPorFila() != dto.getAsientosPorFila();
        boolean sinAsientos = existente.getAsientos() == null || existente.getAsientos().isEmpty();

        aplicarDatos(existente, dto);

        if (cambioGrilla || sinAsientos) {
            if (!sinAsientos && entradaRepository.existsByAsiento_Sala_Id(id)) {
                throw new IllegalArgumentException(
                        "No se puede cambiar la cantidad de asientos: la sala ya tiene entradas vendidas.");
            }
            if (!sinAsientos) {
                asientoRepository.deleteAll(existente.getAsientos());
                existente.getAsientos().clear();
            }
            existente.setAsientos(generarAsientos(existente, dto.getFilas(), dto.getAsientosPorFila()));
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
        asientoRepository.deleteAll(sala.getAsientos());
        salaRepository.delete(sala);
    }

    public void deleteAll() {
        salaRepository.deleteAll();
    }

    /**
     * Si la sala tiene grilla configurada pero no hay butacas, las genera.
     * Sirve para salas creadas antes de persistir asientos individuales.
     */
    public void asegurarAsientos(Long salaId) {
        Salas sala = buscarOFallar(salaId);
        if (sala.getFilas() < 1 || sala.getAsientosPorFila() < 1) {
            return;
        }
        List<Asiento> existentes = asientoRepository.findBySala_Id(salaId);
        if (!existentes.isEmpty()) {
            return;
        }
        sala.setAsientos(generarAsientos(sala, sala.getFilas(), sala.getAsientosPorFila()));
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

    // 0 -> "A", 25 -> "Z", 26 -> "AA"
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
