package br.gov.mt.seplag.seletivo.security.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record AuthRequestDTO(
        @Schema(description = "Usuario", example = "admin")
        @NotBlank String username,
        @Schema(description = "Senha", example = "admin")
        @NotBlank String password
) {
}