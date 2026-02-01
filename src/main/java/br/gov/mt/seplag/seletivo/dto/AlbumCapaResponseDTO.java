package br.gov.mt.seplag.seletivo.dto;

public record AlbumCapaResponseDTO(
        Long id,
        String objectKey,
        Boolean principal,
        String url
) {}