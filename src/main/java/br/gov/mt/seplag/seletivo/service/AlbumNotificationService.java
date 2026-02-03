package br.gov.mt.seplag.seletivo.service;

import br.gov.mt.seplag.seletivo.domain.entity.Album;
import br.gov.mt.seplag.seletivo.domain.entity.Artista;
import br.gov.mt.seplag.seletivo.dto.AlbumNotificationDTO;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AlbumNotificationService {

    private final SimpMessagingTemplate messagingTemplate;

    public AlbumNotificationService(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    public void notifyAlbumCreated(Album album) {
        List<String> artistas = album.getArtistas()
                .stream()
                .map(Artista::getNome)
                .sorted()
                .toList();

        AlbumNotificationDTO payload = new AlbumNotificationDTO(
                album.getId(),
                album.getTitulo(),
                album.getAnoLancamento(),
                artistas,
                LocalDateTime.now()
        );
        messagingTemplate.convertAndSend("/topic/albuns", payload);
    }
}