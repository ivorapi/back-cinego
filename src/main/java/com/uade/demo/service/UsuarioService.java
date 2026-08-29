package com.uade.demo.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.uade.demo.model.Usuarios;
import com.uade.demo.repository.UsuarioRepository;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public List<Usuarios> findAll() {
        return usuarioRepository.findAll();
    }
    
    public Usuarios findById(Long id) {
        return usuarioRepository.findById(id).orElse(null);
    }

    public Usuarios save(Usuarios usuario) {
        return usuarioRepository.save(usuario);
    }

    public void deleteById(Long id) {
        usuarioRepository.deleteById(id);
    }

    public void deleteAll() {
        usuarioRepository.deleteAll();
    }
    
}