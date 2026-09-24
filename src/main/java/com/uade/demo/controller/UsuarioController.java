package com.uade.demo.controller;

import com.uade.demo.dto.CrearAdminRequestDTO;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.uade.demo.dto.LoginRequestDTO;
import com.uade.demo.dto.RegistroRequestDTO;
import com.uade.demo.exception.ResourceNotFoundException;
import com.uade.demo.model.Usuarios;
import com.uade.demo.service.UsuarioService;

import java.util.List;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/api/usuarios")
@Tag(name = "Usuarios", description = "Autenticación y administración de usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    // devolver a todos los usuarios
    // get localhost:8080/api/usuarios
    @GetMapping
    public List<Usuarios> getAllUsuarios() {
        return usuarioService.findAll();
    }

    //Buscar por id
    // get localhost:8080/api/usuarios/1
    @GetMapping("/{id}")
    public ResponseEntity<Usuarios> getUsuarioById(@PathVariable Long id) {
        if (usuarioService.findById(id) == null) {
            throw new ResourceNotFoundException(id);
        }

        return ResponseEntity.ok(usuarioService.findById(id));
    }

    //crear usuario
    // post localhost:8080/api/usuarios
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @PostMapping
    public ResponseEntity<Usuarios> createUsuario(@RequestBody Usuarios usuario) {

        if (usuario.getEmail() == null || usuario.getEmail().isEmpty()) {
            throw new IllegalArgumentException("El email del usuario es requerido");
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(usuarioService.save(usuario));
    }

    //actualizar usuario por id
    // put localhost:8080/api/usuarios/1
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @PutMapping("/{id}")
    public Usuarios updateUsuario(@PathVariable Long id, @RequestBody Usuarios usuario) {
        Usuarios existingUsuario = usuarioService.findById(id);
        if (existingUsuario != null) {
            // existingUsuario.setUsername(usuario.getUsername());
            existingUsuario.setEmail(usuario.getEmail());
            // existingUsuario.setPassword(usuario.getPassword());
            existingUsuario.setNombre(usuario.getNombre());
            existingUsuario.setApellido(usuario.getApellido());
            if (usuario.getPasswordHash() != null) {
                existingUsuario.setPasswordHash(usuario.getPasswordHash());
            }
            return usuarioService.save(existingUsuario);
        } else {
            return null;
        }
    }

    //borrar usuario por id
    // delete localhost:8080/api/usuarios/1
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @DeleteMapping("/{id}")
    public void deleteUsuario(@PathVariable Long id) {
        usuarioService.deleteById(id);
    }

    //elminiar a todos los usuarios
    // delete localhost:8080/api/usuarios
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @DeleteMapping
    public void deleteAllUsuarios() {
        usuarioService.deleteAll();
    }

    //registrar usuario nuevo
    // post localhost:8080/api/usuarios/registro
    @PostMapping("/registro")
    @Operation(summary = "Registrar un cliente", description = "Endpoint público para crear una cuenta con rol CLIENTE.")
    @SecurityRequirements
    public ResponseEntity<Usuarios> registrarUsuario(@RequestBody RegistroRequestDTO request) {
        Usuarios usuarioCreado = usuarioService.registrar(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(usuarioCreado);
    }

    //login de usuario
    // post localhost:8080/api/usuarios/login
    @PostMapping("/login")
    @Operation(summary = "Iniciar sesión", description = "Endpoint público. Devuelve un JWT para usar como Bearer token.")
    @SecurityRequirements
    public ResponseEntity<String> loginUsuario(@RequestBody LoginRequestDTO request) {
        String token = usuarioService.login(request);
        return ResponseEntity.ok(token);
    }

    // crear un usuario admin
    // post localhost:8080/api/usuarios/crearAdmin
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @PostMapping("/crear-admin")
    public ResponseEntity<Usuarios> crearAdmin(@RequestBody CrearAdminRequestDTO request) {
        Usuarios adminCreado = usuarioService.crearAdmin(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(adminCreado);
    }

}
