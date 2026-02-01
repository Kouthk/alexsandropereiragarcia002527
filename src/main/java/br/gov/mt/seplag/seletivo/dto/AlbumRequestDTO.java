package br.gov.mt.seplag.seletivo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;
import java.util.Set;

public record AlbumRequestDTO(
        @NotBlank String titulo,
        Integer anoLancamento,
        @NotEmpty Set<Long> artistasIds,
        List<AlbumCapaRequestDTO>capas
) {}