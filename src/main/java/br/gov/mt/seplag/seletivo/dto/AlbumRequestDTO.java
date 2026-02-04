package br.gov.mt.seplag.seletivo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;
import java.util.Set;

public record AlbumRequestDTO(
        @Schema(description = "Título do álbum", example = "Harakiri")
        @NotBlank String titulo,
        @Schema(description = "Ano de lançamento", example = "2012")
        Integer anoLancamento,
        @Schema(description = "IDs dos artistas vinculados", example = "[1, 2]")
        @NotEmpty Set<Long> artistasIds,
        @Schema(description = "Lista de capas já armazenadas no MinIO")
        List<AlbumCapaRequestDTO> capas) {}