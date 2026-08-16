package com.uade.demo.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table
public class Usuarios {
    @Id
   private Long id;
   @Column
   private String nombre;
   @Column
   private String apellido;
   @Column
   private String mail;
}
