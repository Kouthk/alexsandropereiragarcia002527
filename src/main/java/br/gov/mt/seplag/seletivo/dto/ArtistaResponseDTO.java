package br.gov.mt.seplag.seletivo.dto;

import br.gov.mt.seplag.seletivo.domain.enums.TipoArtistaEnum;

public record ArtistaResponseDTO(
        Long id,
        String nome,
        TipoArtistaEnum tipo,
        Boolean ativo
) {}
