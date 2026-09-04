package com.uade.demo.service;

import java.util.List;
import org.springframework.stereotype.Service;
import com.uade.demo.model.Funcion;
import com.uade.demo.repository.FuncionRepository;


@Service
public class FuncionService {
    private final FuncionRepository funcionRepository;

    public FuncionService(FuncionRepository funcionRepository) {
        this.funcionRepository = funcionRepository;
    }
    public List<Funcion> getAllFunciones() {
        return funcionRepository.findAll();
    }

    public Funcion getFuncionById(Long id) {
        return funcionRepository.findById(id).orElse(null);
    }

    public Funcion saveFuncion(Funcion funcion) {
        return funcionRepository.save(funcion);
    }

    public void deleteFuncion(Long id) {
        funcionRepository.deleteById(id);
    }

    public void deleteAllFunciones() {
        funcionRepository.deleteAll();
    }
}