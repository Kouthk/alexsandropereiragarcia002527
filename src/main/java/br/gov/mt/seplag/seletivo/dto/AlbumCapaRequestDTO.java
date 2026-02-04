package br.gov.mt.seplag.seletivo.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record AlbumCapaRequestDTO(
        @Schema(description = "Chave do objeto no MinIO", example = "albuns/uuid-capa.jpg")
        String objectKey,
        @Schema(description = "Define se esta capa é a principal", example = "true")
        Boolean principal
) {
}