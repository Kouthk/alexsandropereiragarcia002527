package br.gov.mt.seplag.seletivo.dto;

import java.time.LocalDateTime;
import java.util.List;

public record AlbumNotificationDTO(
        Long id,
        String titulo,
        Integer anoLancamento,
        List<String> artistas,
        LocalDateTime criadoEm
) {
}