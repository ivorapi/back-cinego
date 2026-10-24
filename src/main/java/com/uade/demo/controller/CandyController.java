package com.uade.demo.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.uade.demo.dto.CandyRequestDTO;
import com.uade.demo.dto.CandyResponseDTO;
import com.uade.demo.service.CandyService;

@RestController
@RequestMapping("/api/candy")
public class CandyController {

    private final CandyService candyService;

    public CandyController(CandyService candyService) {
        this.candyService = candyService;
    }

    @GetMapping
    public ResponseEntity<List<CandyResponseDTO>> getAll() {
        return ResponseEntity.ok(candyService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CandyResponseDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(candyService.findById(id));
    }

    @PostMapping
    public ResponseEntity<CandyResponseDTO> create(@RequestBody CandyRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(candyService.save(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CandyResponseDTO> update(@PathVariable Long id, @RequestBody CandyRequestDTO dto) {
        return ResponseEntity.ok(candyService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        candyService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteAll() {
        candyService.deleteAll();
        return ResponseEntity.noContent().build();
    }
}
