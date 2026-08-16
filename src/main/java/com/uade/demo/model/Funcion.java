package com.uade.demo.model;

import java.sql.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;

@Getter
@Table
@Entity
public class Funcion {
    @Id
    private Long id;    
    @Column
    private Date fecha;
    @Column
    private String titulo;
    @Column 
    private Double precio;

}
