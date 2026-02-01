package br.gov.mt.seplag.seletivo.controller;

import br.gov.mt.seplag.seletivo.domain.entity.AlbumCapa;
import br.gov.mt.seplag.seletivo.dto.AlbumCapaResponseDTO;
import br.gov.mt.seplag.seletivo.service.AlbumCapaService;
import br.gov.mt.seplag.seletivo.service.MinioStorageService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/v1/albuns/{albumId}/capas")
public class AlbumCapaController {

    private final AlbumCapaService albumCapaService;
    private final MinioStorageService minioStorageService;

    public AlbumCapaController(
            AlbumCapaService albumCapaService,
            MinioStorageService minioStorageService
    ) {
        this.albumCapaService = albumCapaService;
        this.minioStorageService = minioStorageService;
    }

    @GetMapping
    public ResponseEntity<List<AlbumCapaResponseDTO>> listar(@PathVariable Long albumId) {
        List<AlbumCapaResponseDTO> capas = albumCapaService.listarPorAlbum(albumId)
                .stream()
                .map(this::toResponse)
                .toList();
        return ResponseEntity.ok(capas);
    }

    @PostMapping("/upload")
    public ResponseEntity<AlbumCapaResponseDTO> upload(
            @PathVariable Long albumId,
            @RequestParam("file") MultipartFile file,
            @RequestParam(defaultValue = "false") boolean principal
    ) {
        String objectKey = minioStorageService.uploadAlbumCapa(file);
        AlbumCapa capa = albumCapaService.adicionarCapa(albumId, objectKey, principal);
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(capa));
    }

    @PutMapping("/{capaId}/principal")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void definirPrincipal(@PathVariable Long albumId, @PathVariable Long capaId) {
        albumCapaService.definirComoPrincipal(capaId);
    }

    @DeleteMapping("/{capaId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void remover(@PathVariable Long albumId, @PathVariable Long capaId) {
        albumCapaService.remover(capaId);
    }

    private AlbumCapaResponseDTO toResponse(AlbumCapa capa) {
        return new AlbumCapaResponseDTO(
                capa.getId(),
                capa.getObjectKey(),
                capa.getPrincipal(),
                minioStorageService.gerarUrlPresignada(capa.getObjectKey())
        );
    }
}