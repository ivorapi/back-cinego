package com.uade.demo.Controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.uade.demo.Models.UsuarioModel;
import com.uade.demo.Services.UsuarioService;


import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;



@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {


    

    private final UsuarioService UsuarioService;

    public UsuarioController(UsuarioService UsuarioService) {
        this.UsuarioService = UsuarioService;
    }
    // devolver a todos los usuarios
    // get localhost:8080/api/usuarios
    @GetMapping
    public List<UsuarioModel> getAllUsuarios() {
        return UsuarioService.findAll();
    }

    //Buscar por id
    // get localhost:8080/api/usuarios/1
    @GetMapping("/{id}")
    public UsuarioModel getUsuarioById(@PathVariable Long id) {
        return UsuarioService.findById(id);
    }

    //crear usuario
    // post localhost:8080/api/usuarios
    
    @PostMapping
    public UsuarioModel createUsuario(@RequestBody UsuarioModel usuario) {
        return UsuarioService.save(usuario);
    }

    //actualizar usuario por id
    // put localhost:8080/api/usuarios/1
    
    @PutMapping("/{id}")
    public UsuarioModel updateUsuario(@PathVariable Long id, @RequestBody UsuarioModel usuario) {
        UsuarioModel existingUsuario = UsuarioService.findById(id);
        if (existingUsuario != null) {
            existingUsuario.setUsername(usuario.getUsername());
            existingUsuario.setMail(usuario.getMail());
            existingUsuario.setPassword(usuario.getPassword());
            existingUsuario.setNombre(usuario.getNombre());
            existingUsuario.setApellido(usuario.getApellido());
            return UsuarioService.save(existingUsuario);
        } else {
            return null;
        }
    }

    //borrar usuario por id
    // delete localhost:8080/api/usuarios/1

    @DeleteMapping("/{id}")
    public void deleteUsuario(@PathVariable Long id) {
        UsuarioService.deleteById(id);
    }

    //elminiar a todos los usuarios
    // delete localhost:8080/api/usuarios
    @DeleteMapping
    public void deleteAllUsuarios() {
        UsuarioService.deleteAll();
    }

}
