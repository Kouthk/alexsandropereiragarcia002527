package br.gov.mt.seplag.seletivo.dto;

import br.gov.mt.seplag.seletivo.domain.enums.TipoArtistaEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ArtistaRequestDTO(
        @Schema(description = "Nome do artista", example = "Serj Tankian")
        @NotBlank String nome,
        @Schema(description = "Tipo do artista", allowableValues = {"SOLO", "BANDA"})
        @NotNull TipoArtistaEnum tipo
) {}