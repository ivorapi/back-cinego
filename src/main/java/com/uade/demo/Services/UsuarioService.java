package com.uade.demo.Services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.uade.demo.Models.UsuarioModel;
import com.uade.demo.Repository.UsuarioRepository;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public List<UsuarioModel> findAll() {
        return usuarioRepository.findAll();
    }
    
    public UsuarioModel findById(Long id) {
        return usuarioRepository.findById(id).orElse(null);
    }

    public UsuarioModel save(UsuarioModel usuario) {
        return usuarioRepository.save(usuario);
    }

    public void deleteById(Long id) {
        usuarioRepository.deleteById(id);
    }

    public void deleteAll() {
        usuarioRepository.deleteAll();
    }
    
}
