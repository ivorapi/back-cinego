package com.uade.demo.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.uade.demo.Models.UsuarioModel;

public interface UsuarioRepository extends JpaRepository<UsuarioModel, Long> {
    // public List<UsuarioModel> findAll(){
    //     String sql = "SELECT * FROM usuarios";
    //     return null;
    // }
    
}
