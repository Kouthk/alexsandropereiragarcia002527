package br.gov.mt.seplag.seletivo.dto;

public record ArtistaResponseDTO(
        Long id,
        String nome,
        String tipo,
        Boolean ativo
) {}
