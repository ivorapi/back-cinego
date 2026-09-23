package com.uade.demo.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ReservaRequestDTO {
    private Long usuarioId;
    private Long funcionId;
}
