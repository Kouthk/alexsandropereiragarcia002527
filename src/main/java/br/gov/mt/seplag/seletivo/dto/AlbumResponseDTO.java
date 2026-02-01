package br.gov.mt.seplag.seletivo.dto;

import java.util.List;

public record AlbumResponseDTO(
        Long id,
        String titulo,
        Integer anoLancamento,
        List<String> artistas,
        List<AlbumCapaResponseDTO> capas
) {}