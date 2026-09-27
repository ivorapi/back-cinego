package com.uade.demo.service;

import java.util.List;

import com.uade.demo.dto.CrearAdminRequestDTO;
import com.uade.demo.model.Rol;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.uade.demo.dto.LoginRequestDTO;
import com.uade.demo.dto.RegistroRequestDTO;
import com.uade.demo.model.Usuarios;
import com.uade.demo.repository.UsuarioRepository;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public UsuarioService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder, JwtService jwtService, AuthenticationManager authenticationManager) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
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

    public Usuarios registrar(RegistroRequestDTO request) {
        if (usuarioRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Ya existe un usuario registrado con ese email");
        }

        Usuarios usuario = new Usuarios();
        usuario.setEmail(request.getEmail());
        usuario.setNombre(request.getNombre());
        usuario.setApellido(request.getApellido());
        usuario.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        usuario.setFechaNacimiento(request.getFechaNacimiento());
        usuario.setSexo(request.getSexo());
        usuario.setRol(Rol.CLIENTE);

        return usuarioRepository.save(usuario);
    }

    public String login(LoginRequestDTO request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

        Usuarios usuario = usuarioRepository.findByEmail(request.getEmail()).orElseThrow();

        return jwtService.generarToken(usuario.getEmail(), usuario.getRol());
    }

    public Usuarios crearAdmin(CrearAdminRequestDTO request) {
        if (usuarioRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Ya existe un usuario registrado con ese email");
        }

        Usuarios admin = new Usuarios();
        admin.setEmail(request.getEmail());
        admin.setNombre(request.getNombre());
        admin.setApellido(request.getApellido());
        admin.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        admin.setRol(Rol.ADMIN);

        return usuarioRepository.save(admin);
    }

}
