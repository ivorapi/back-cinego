package com.uade.demo.controller;

import com.uade.demo.dto.CrearAdminRequestDTO;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.uade.demo.dto.LoginRequestDTO;
import com.uade.demo.dto.RegistroRequestDTO;
import com.uade.demo.dto.UsuarioResponseDTO;
import com.uade.demo.exception.ResourceNotFoundException;
import com.uade.demo.model.Usuarios;
import com.uade.demo.service.UsuarioService;

import java.time.Duration;
import java.util.List;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
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

    // cuánto dura el token (en milisegundos), lo usamos también para la cookie
    @Value("${jwt.expiration}")
    private long expiration;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    // devolver a todos los usuarios
    // get localhost:8080/api/usuarios
    @GetMapping
    public List<Usuarios> getAllUsuarios() {
        return usuarioService.findAll();
    }

    // devolver el usuario logueado (sale del token de la cookie o del header)
    // get localhost:8080/api/usuarios/me
    @GetMapping("/me")
    @Operation(summary = "Usuario actual", description = "Devuelve los datos del usuario logueado. Sirve para que el front sepa quién está logueado sin leer el token.")
    public ResponseEntity<UsuarioResponseDTO> getUsuarioActual(Authentication authentication) {
        // el filtro JWT guarda el email del usuario como "name" de la autenticación
        Usuarios usuario = usuarioService.findByEmail(authentication.getName());
        return ResponseEntity.ok(new UsuarioResponseDTO(usuario));
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
    @PostMapping
    public ResponseEntity<Usuarios> createUsuario(@RequestBody Usuarios usuario) {

        if (usuario.getEmail() == null || usuario.getEmail().isEmpty()) {
            throw new IllegalArgumentException("El email del usuario es requerido");
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(usuarioService.save(usuario));
    }

    //actualizar usuario por id
    // put localhost:8080/api/usuarios/1
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
    @DeleteMapping("/{id}")
    public void deleteUsuario(@PathVariable Long id) {
        usuarioService.deleteById(id);
    }

    //elminiar a todos los usuarios
    // delete localhost:8080/api/usuarios
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
    @Operation(summary = "Iniciar sesión", description = "Endpoint público. Devuelve un JWT para usar como Bearer token y además lo guarda en una cookie HttpOnly llamada 'token'.")
    @SecurityRequirements
    public ResponseEntity<String> loginUsuario(@RequestBody LoginRequestDTO request) {
        String token = usuarioService.login(request);

        ResponseCookie cookie = ResponseCookie
                .from("token", token)
                .httpOnly(true)
                .secure(false)
                .path("/")
                .sameSite("Lax")
                .maxAge(Duration.ofMillis(expiration))
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(token);
    }

    // logout de usuario: borra la cookie del token
    // post localhost:8080/api/usuarios/logout
    @PostMapping("/logout")
    @Operation(summary = "Cerrar sesión", description = "Endpoint público. Borra la cookie HttpOnly 'token'.")
    @SecurityRequirements
    public ResponseEntity<Void> logoutUsuario() {
        // misma cookie pero vacía y con maxAge 0: el navegador la borra
        ResponseCookie cookie = ResponseCookie
                .from("token", "")
                .httpOnly(true)
                .secure(false)
                .path("/")
                .sameSite("Lax")
                .maxAge(0)
                .build();

        return ResponseEntity.noContent()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .build();
    }

    // crear un usuario admin
    // post localhost:8080/api/usuarios/crearAdmin
    @PostMapping("/crear-admin")
    public ResponseEntity<Usuarios> crearAdmin(@RequestBody CrearAdminRequestDTO request) {
        Usuarios adminCreado = usuarioService.crearAdmin(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(adminCreado);
    }

}
