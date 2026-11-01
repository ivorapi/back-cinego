package com.uade.demo.service;

import java.util.List;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.uade.demo.dto.CarritoRequestDTO;
import com.uade.demo.dto.CarritoResponseDTO;
import com.uade.demo.exception.ResourceNotFoundException;
import com.uade.demo.model.Candy;
import com.uade.demo.model.Carrito;
import com.uade.demo.model.Usuarios;
import com.uade.demo.repository.CandyRepository;
import com.uade.demo.repository.CarritoRepository;
import com.uade.demo.repository.UsuarioRepository;

@Service
public class CarritoService {

    private final CarritoRepository carritoRepository;
    private final CandyRepository candyRepository;
    private final UsuarioRepository usuarioRepository;

    public CarritoService(CarritoRepository carritoRepository, CandyRepository candyRepository,
            UsuarioRepository usuarioRepository) {
        this.carritoRepository = carritoRepository;
        this.candyRepository = candyRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional(readOnly = true)
    public List<CarritoResponseDTO> getAll() {
        Usuarios usuario = obtenerUsuarioAutenticado();
        return carritoRepository.findByUsuario_Id(usuario.getId()).stream()
                .map(CarritoResponseDTO::new)
                .toList();
    }

    @Transactional(readOnly = true)
    public CarritoResponseDTO getById(Long id) {
        return new CarritoResponseDTO(buscarItemPropio(id));
    }

    @Transactional
    public CarritoResponseDTO add(CarritoRequestDTO dto) {
        Usuarios usuario = obtenerUsuarioAutenticado();
        Candy candy = candyRepository.findById(dto.getCandyId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No se encontró el producto de candy con id: " + dto.getCandyId()));

        Carrito item = carritoRepository.findByUsuario_IdAndCandy_Id(usuario.getId(), candy.getId())
                .orElse(null);

        if (item == null) {
            item = new Carrito();
            item.setUsuario(usuario);
            item.setCandy(candy);
            item.setCantidad(dto.getCantidad());
        } else {
            item.setCantidad(item.getCantidad() + dto.getCantidad());
        }

        return new CarritoResponseDTO(carritoRepository.save(item));
    }

    @Transactional
    public void delete(Long id) {
        carritoRepository.delete(buscarItemPropio(id));
    }

    private Carrito buscarItemPropio(Long id) {
        Carrito item = carritoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No se encontró el ítem del carrito con id: " + id));

        if (!item.getUsuarioId().equals(obtenerUsuarioAutenticado().getId())) {
            throw new AccessDeniedException("No tenés permisos para acceder a este ítem del carrito");
        }
        return item;
    }

    private Usuarios obtenerUsuarioAutenticado() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null
                || !authentication.isAuthenticated()
                || "anonymousUser".equals(authentication.getPrincipal())) {
            throw new IllegalArgumentException("Debés iniciar sesión para usar el carrito");
        }

        return usuarioRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No se encontró el usuario autenticado"));
    }
}
