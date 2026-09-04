package com.uade.demo.service;

import java.util.List;
import org.springframework.stereotype.Service;
import com.uade.demo.model.Funcion;
import com.uade.demo.repository.FuncionRepository;


@Service
public class FuncionServices {
    private final FuncionRepository funcionRepository;

    public FuncionServices(FuncionRepository funcionRepository) {
        this.funcionRepository = funcionRepository;
    }
    public List<Funcion> getAllFunciones() {
        return funcionRepository.findAll();
    }
    public void deleteFuncion(Long id) {
        funcionRepository.deleteById(id);
    }
}