package br.gov.mt.seplag.seletivo.dto;

public record RegionalResponseDTO(
        Long id,
        Integer idRegionalExterno,
        String nome,
        Boolean ativo
) {
}