package br.gov.mt.seplag.seletivo.dto;

import br.gov.mt.seplag.seletivo.domain.enums.TipoArtistaEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ArtistaRequestDTO(
        @NotBlank String nome,
        @Schema(allowableValues = {"SOLO", "BANDA"})
        @NotNull TipoArtistaEnum tipo
) {}