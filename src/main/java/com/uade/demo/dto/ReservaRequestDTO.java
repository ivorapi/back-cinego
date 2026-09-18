package com.uade.demo.dto;

import java.util.List;

import lombok.Data;

@Data
public class ReservaRequestDTO {
    Long funcionId;
    List<Long> asientoId;
}
