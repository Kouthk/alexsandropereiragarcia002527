package br.gov.mt.seplag.seletivo.dto;

import br.gov.mt.seplag.seletivo.domain.enums.TipoArtistaEnum;
import io.swagger.v3.oas.annotations.media.Schema;

public record ArtistaResponseDTO(
        @Schema(description = "ID do artista", example = "1")
        Long id,
        @Schema(description = "Nome do artista", example = "Serj Tankian")
        String nome,
        @Schema(description = "Tipo do artista", example = "SOLO")
        TipoArtistaEnum tipo,
        @Schema(description = "Indica se o artista está ativo", example = "true")
        Boolean ativo
) {}
