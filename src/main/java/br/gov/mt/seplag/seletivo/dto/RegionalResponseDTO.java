package br.gov.mt.seplag.seletivo.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record RegionalResponseDTO(
        @Schema(description = "ID interno", example = "10")
        Long id,
        @Schema(description = "ID no serviço externo", example = "123")
        Integer idRegionalExterno,
        @Schema(description = "Nome da regional", example = "Cuiabá")
        String nome,
        @Schema(description = "Indica se está ativa", example = "true")
        Boolean ativo
) {
}