package com.uade.demo.service;

import java.math.BigDecimal;
import java.util.List;
import org.springframework.stereotype.Service;
import com.uade.demo.exception.ResourceNotFoundException;
import com.uade.demo.model.Funcion;
import com.uade.demo.repository.FuncionRepository;


@Service
public class FuncionServices {
    private final FuncionRepository funcionRepository;
    private final DescuentoService descuentoService;

    public FuncionServices(FuncionRepository funcionRepository, DescuentoService descuentoService) {
        this.funcionRepository = funcionRepository;
        this.descuentoService = descuentoService;
    }
    public List<Funcion> getAllFunciones() {
        return funcionRepository.findAll();
    }
    public void deleteFuncion(Long id) {
        funcionRepository.deleteById(id);
    }
    public BigDecimal calcularPrecioFinal(Long funcionId) {
        Funcion funcion = funcionRepository.findById(funcionId)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró la función con id: " + funcionId));
        return descuentoService.calcularPrecioFinal(funcion);
    }
}