package br.gov.mt.seplag.seletivo.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

public record AlbumResponseDTO(
        @Schema(description = "ID do álbum", example = "10")
        Long id,
        @Schema(description = "Título do álbum", example = "Harakiri")
        String titulo,
        @Schema(description = "Ano de lançamento", example = "2012")
        Integer anoLancamento,
        @Schema(description = "Lista de nomes dos artistas")
        List<String> artistas,
        @Schema(description = "Capas do álbum com URLs pré-assinadas")
        List<AlbumCapaResponseDTO> capas
) {}