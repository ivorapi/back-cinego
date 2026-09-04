package com.uade.demo.dto;

import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class AsociarPeliculasRequestDTO {
    private List<Long> peliculaIds;
}
