package br.gov.mt.seplag.seletivo.dto;

import br.gov.mt.seplag.seletivo.domain.enums.TipoArtistaEnum;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ArtistaRequestDTO(
        @NotBlank String nome,
        @NotNull TipoArtistaEnum tipo
) {}