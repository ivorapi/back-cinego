package com.uade.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.uade.demo.model.Usuarios;

public interface UsuarioRepository extends JpaRepository<Usuarios, Long> {
    // public List<UsuarioModel> findAll(){
    //     String sql = "SELECT * FROM usuarios";
    //     return null;
    // }
    
}