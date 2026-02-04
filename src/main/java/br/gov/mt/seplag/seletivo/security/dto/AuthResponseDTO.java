package br.gov.mt.seplag.seletivo.security.dto;

public record AuthResponseDTO(
        String accessToken,
        String refreshToken,
        String tokenType,
        long expiresInSeconds
) {
}