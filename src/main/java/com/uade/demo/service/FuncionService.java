package com.uade.demo.service;

import java.util.List;

import org.springframework.stereotype.Service;

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
public class FuncionService {
    private final FuncionRepository funcionRepository;
    private final PeliculaRepository peliculaRepository;
    private final SalaRepository salaRepository;

    public FuncionService(FuncionRepository funcionRepository, PeliculaRepository peliculaRepository,
            SalaRepository salaRepository) {
        this.funcionRepository = funcionRepository;
        this.peliculaRepository = peliculaRepository;
        this.salaRepository = salaRepository;
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