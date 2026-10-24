package com.uade.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.uade.demo.model.Candy;

public interface CandyRepository extends JpaRepository<Candy, Long> {
}
