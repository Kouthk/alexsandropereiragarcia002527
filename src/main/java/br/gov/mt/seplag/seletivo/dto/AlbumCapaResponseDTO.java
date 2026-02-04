package br.gov.mt.seplag.seletivo.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record AlbumCapaResponseDTO(
        @Schema(description = "ID da capa", example = "5")
        Long id,
        @Schema(description = "Chave do objeto no MinIO", example = "albuns/uuid-capa.jpg")
        String objectKey,
        @Schema(description = "Indica se a capa é principal", example = "false")
        Boolean principal,
        @Schema(description = "URL pré-assinada para download", example = "https://minio/.../albuns/uuid-capa.jpg?X-Amz-...")
        String url
) {}