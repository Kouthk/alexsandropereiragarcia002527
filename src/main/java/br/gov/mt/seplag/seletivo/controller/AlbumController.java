package br.gov.mt.seplag.seletivo.controller;

import br.gov.mt.seplag.seletivo.domain.entity.Album;
import br.gov.mt.seplag.seletivo.domain.entity.Artista;
import br.gov.mt.seplag.seletivo.dto.AlbumCapaResponseDTO;
import br.gov.mt.seplag.seletivo.dto.AlbumRequestDTO;
import br.gov.mt.seplag.seletivo.dto.AlbumResponseDTO;
import br.gov.mt.seplag.seletivo.service.AlbumService;
import br.gov.mt.seplag.seletivo.service.MinioStorageService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/v1/albuns")
public class AlbumController {

    private final AlbumService albumService;
    private final MinioStorageService minioStorageService;

    public AlbumController(AlbumService albumService, MinioStorageService minioStorageService) {
        this.albumService = albumService;
        this.minioStorageService = minioStorageService;
    }

    @GetMapping
    public ResponseEntity<Page<AlbumResponseDTO>> listar(Pageable pageable) {
        Page<Album> albuns = albumService.listar(pageable);
        return ResponseEntity.ok(mapPage(albuns));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AlbumResponseDTO> buscarPorId(@PathVariable Long id) {
        Album album = albumService.buscarPorId(id);
        return ResponseEntity.ok(toResponse(album));
    }

    @GetMapping("/por-artista/{artistaId}")
    public ResponseEntity<Page<AlbumResponseDTO>> listarPorArtista(
            @PathVariable Long artistaId,
            Pageable pageable
    ) {
        Page<Album> albuns = albumService.listarPorArtista(artistaId, pageable);
        return ResponseEntity.ok(mapPage(albuns));
    }

    @PostMapping
    public ResponseEntity<AlbumResponseDTO> criar(
            @Valid @RequestBody AlbumRequestDTO request
    ) {
        Album album = new Album();
        album.setTitulo(request.titulo());
        album.setAnoLancamento(request.anoLancamento());

        Album criado = albumService.criar(album, request.artistasIds(), request.capas());
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(criado));
    }

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.MULTIPART_FORM_DATA_VALUE )
    public ResponseEntity<AlbumResponseDTO> criarComCapas(
            @RequestParam String titulo,
            @RequestParam(required = false) Integer anoLancamento,
            @RequestParam Set<Long> artistasIds,
            @RequestParam(name = "files", required = false) List<MultipartFile> files,
            @RequestParam(defaultValue = "false") boolean principal
    ) {
        Album album = new Album();
        album.setTitulo(titulo);
        album.setAnoLancamento(anoLancamento);

        Album criado = albumService.criarComCapas(album, artistasIds, files, principal);
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(criado));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AlbumResponseDTO> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody AlbumRequestDTO request
    ) {
        Album album = new Album();
        album.setTitulo(request.titulo());
        album.setAnoLancamento(request.anoLancamento());

        Album atualizado = albumService.atualizar(id, album);
        return ResponseEntity.ok(toResponse(atualizado));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void inativar(@PathVariable Long id) {
        albumService.inativar(id);
    }

    private Page<AlbumResponseDTO> mapPage(Page<Album> albuns) {
        List<AlbumResponseDTO> conteudo = albuns.stream()
                .map(this::toResponse)
                .toList();
        return new PageImpl<>(conteudo, albuns.getPageable(), albuns.getTotalElements());
    }

    private AlbumResponseDTO toResponse(Album album) {
        List<String> artistas = album.getArtistas()
                .stream()
                .map(Artista::getNome)
                .sorted()
                .toList();

        List<AlbumCapaResponseDTO> capas = album.getCapas() == null
                ? List.of()
                : album.getCapas()
                .stream()
                .map(capa -> new AlbumCapaResponseDTO(
                        capa.getId(),
                        capa.getObjectKey(),
                        capa.getPrincipal(),
                        minioStorageService.gerarUrlPresignada(capa.getObjectKey())
                ))
                .toList();

        return new AlbumResponseDTO(
                album.getId(),
                album.getTitulo(),
                album.getAnoLancamento(),
                artistas,
                capas
        );
    }
}