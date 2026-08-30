package com.uade.demo.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.uade.demo.model.Usuarios;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuarios, Long> {
    // public List<UsuarioModel> findAll(){
    //     String sql = "SELECT * FROM usuarios";
    //     return null;
    // }
    Optional<Usuarios> findByEmail(String email);
}