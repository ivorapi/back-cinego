package com.uade.demo.service;

import java.util.List;
import java.util.Comparator;
import java.util.HashSet;
import com.uade.demo.dto.AsientoResponseDTO;
import com.uade.demo.model.Asiento;
import com.uade.demo.model.EstadoReserva;
import com.uade.demo.repository.AsientoRepository;
import com.uade.demo.repository.EntradaRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.uade.demo.dto.funcion.FuncionRequestDTO;
import com.uade.demo.dto.funcion.FuncionResponseDTO;
import com.uade.demo.exception.ResourceNotFoundException;
import com.uade.demo.model.Funcion;
import com.uade.demo.model.Peliculas;
import com.uade.demo.model.Salas;
import com.uade.demo.repository.FuncionRepository;
import com.uade.demo.repository.PeliculaRepository;
import com.uade.demo.repository.SalaRepository;

@Service
@Transactional
public class FuncionService {
    private final FuncionRepository funcionRepository;
    private final PeliculaRepository peliculaRepository;
    private final SalaRepository salaRepository;
    private final AsientoRepository asientoRepository;
    private final EntradaRepository entradaRepository;

    public FuncionService(FuncionRepository funcionRepository, PeliculaRepository peliculaRepository,
            SalaRepository salaRepository, AsientoRepository asientoRepository, EntradaRepository entradaRepository) {
        this.funcionRepository = funcionRepository;
        this.peliculaRepository = peliculaRepository;
        this.salaRepository = salaRepository;
        this.asientoRepository = asientoRepository;
        this.entradaRepository = entradaRepository;
    }

    @Transactional(readOnly = true)
    public List<AsientoResponseDTO> getAsientos(Long id) {
        Funcion funcion = buscarPorId(id);
        var ocupados = new HashSet<>(entradaRepository.findAsientoIdsOcupados(id, EstadoReserva.CANCELADA));
        return asientoRepository.findBySala_Id(funcion.getSalaId()).stream()
                .sorted(Comparator.comparing(Asiento::getFila).thenComparingInt(Asiento::getNumero))
                .map(a -> new AsientoResponseDTO(a.getId(), a.getFila(), a.getNumero(), ocupados.contains(a.getId())))
                .toList();
    }

    public List<FuncionResponseDTO> getAllFunciones() {
        return funcionRepository.findAll().stream()
                .map(this::toResponseDTO)
                .toList();
    }

    public FuncionResponseDTO getFuncionById(Long id) {
        Funcion funcion = buscarPorId(id);
        return toResponseDTO(funcion);
    }

    public FuncionResponseDTO crearFuncion(FuncionRequestDTO dto) {
        Funcion funcion = new Funcion();
        aplicarDatos(funcion, dto);
        return toResponseDTO(funcionRepository.save(funcion));
    }

    public FuncionResponseDTO actualizarFuncion(Long id, FuncionRequestDTO dto) {
        Funcion funcion = buscarPorId(id);
        aplicarDatos(funcion, dto);
        return toResponseDTO(funcionRepository.save(funcion));
    }

    public void deleteFuncion(Long id) {
        funcionRepository.deleteById(id);
    }

    public void deleteAllFunciones() {
        funcionRepository.deleteAll();
    }

    private Funcion buscarPorId(Long id) {
        return funcionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró la función con id: " + id));
    }

    // Copia los datos del DTO de entrada a la entidad, resolviendo pelicula y sala por id.
    private void aplicarDatos(Funcion funcion, FuncionRequestDTO dto) {
        Peliculas pelicula = peliculaRepository.findById(dto.getPeliculaId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No se encontró la película con id: " + dto.getPeliculaId()));
        Salas sala = salaRepository.findById(dto.getSalaId())
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró la sala con id: " + dto.getSalaId()));

        funcion.setPelicula(pelicula);
        funcion.setSala(sala);
        funcion.setHorarioInicio(dto.getHorarioInicio());
        funcion.setPrecio(dto.getPrecio());
    }

    private FuncionResponseDTO toResponseDTO(Funcion funcion) {
        return new FuncionResponseDTO(
                funcion.getId(),
                funcion.getPeliculaId(),
                funcion.getPelicula() != null ? funcion.getPelicula().getTitulo() : null,
                funcion.getSalaId(),
                funcion.getSala() != null ? funcion.getSala().getNombre() : null,
                funcion.getHorarioInicio(),
                funcion.getPrecio());
    }
}
