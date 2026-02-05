package br.gov.mt.seplag.seletivo.controller;

import br.gov.mt.seplag.seletivo.domain.entity.Album;
import br.gov.mt.seplag.seletivo.domain.entity.Artista;
import br.gov.mt.seplag.seletivo.domain.enums.TipoArtistaEnum;
import br.gov.mt.seplag.seletivo.dto.AlbumCapaResponseDTO;
import br.gov.mt.seplag.seletivo.dto.AlbumRequestDTO;
import br.gov.mt.seplag.seletivo.dto.AlbumResponseDTO;
import br.gov.mt.seplag.seletivo.service.AlbumService;
import br.gov.mt.seplag.seletivo.service.MinioStorageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springdoc.core.annotations.ParameterObject;
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

@Tag(name = "Álbuns", description = "Operações para cadastro e consulta de álbuns")
@RestController
@RequestMapping("/api/v1/albuns")
public class AlbumController {

    private final AlbumService albumService;
    private final MinioStorageService minioStorageService;

    public AlbumController(AlbumService albumService, MinioStorageService minioStorageService) {
        this.albumService = albumService;
        this.minioStorageService = minioStorageService;
    }

    @Operation(summary = "Listar álbuns", description = "Lista álbuns ativos com paginação.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista paginada de álbuns",
                    content = @Content(schema = @Schema(implementation = AlbumResponseDTO.class)))
    })
    @GetMapping
    public ResponseEntity<Page<AlbumResponseDTO>> listar(@ParameterObject Pageable pageable) {
        Page<Album> albuns = albumService.listar(pageable);
        return ResponseEntity.ok(mapPage(albuns));
    }

    @Operation(summary = "Buscar álbum por ID", description = "Retorna um álbum pelo identificador.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Álbum encontrado",
                    content = @Content(schema = @Schema(implementation = AlbumResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Álbum não encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<AlbumResponseDTO> buscarPorId(@PathVariable Long id) {
        Album album = albumService.buscarPorId(id);
        return ResponseEntity.ok(toResponse(album));
    }

    @Operation(summary = "Listar álbuns por artista (ID)",
            description = "Lista álbuns ativos vinculados ao artista informado.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista paginada de álbuns",
                    content = @Content(schema = @Schema(implementation = AlbumResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Artista não encontrado ou sem álbuns")
    })
    @GetMapping("/por-artista/{artistaId}")
    public ResponseEntity<Page<AlbumResponseDTO>> listarPorArtista(
            @PathVariable Long artistaId,
            @ParameterObject Pageable pageable
    ) {
        Page<Album> albuns = albumService.listarPorArtista(artistaId, pageable);
        return ResponseEntity.ok(mapPage(albuns));
    }

    @Operation(summary = "Listar álbuns por nome do artista",
            description = "Busca álbuns por nome do artista (parcial, case-insensitive).")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista paginada de álbuns",
                    content = @Content(schema = @Schema(implementation = AlbumResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Artista sem álbuns")
    })
    @GetMapping("/por-artista")
    public ResponseEntity<Page<AlbumResponseDTO>> listarPorNomeArtista(
            @RequestParam String nome,
            @ParameterObject Pageable pageable
    ) {
        Page<Album> albuns = albumService.listarPorNomeArtista(nome, pageable);
        return ResponseEntity.ok(mapPage(albuns));
    }

    @Operation(summary = "Listar álbuns por tipo de artista",
            description = "Lista álbuns associados a artistas do tipo informado (BANDA ou SOLO).")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista paginada de álbuns",
                    content = @Content(schema = @Schema(implementation = AlbumResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Nenhum álbum encontrado para o tipo informado")
    })
    @GetMapping("/por-tipo")
    public ResponseEntity<Page<AlbumResponseDTO>> listarPorTipoArtista(
            @RequestParam TipoArtistaEnum tipo,
            @ParameterObject Pageable pageable
    ) {
        Page<Album> albuns = albumService.listarPorTipoArtista(tipo, pageable);
        return ResponseEntity.ok(mapPage(albuns));
    }

    @Operation(summary = "Listar álbuns por filtros combinados",
            description = "Filtra por título do álbum, nome do artista e tipo (BANDA ou SOLO).")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista paginada de álbuns",
                    content = @Content(schema = @Schema(implementation = AlbumResponseDTO.class)))
    })
    @GetMapping("/filtro")
    public ResponseEntity<Page<AlbumResponseDTO>> listarPorFiltros(
            @RequestParam String titulo,
            @RequestParam String artista,
            @RequestParam TipoArtistaEnum tipo,
            @ParameterObject Pageable pageable
    ) {
        Page<Album> albuns = albumService.listarPorFiltros(titulo, artista, tipo, pageable);
        return ResponseEntity.ok(mapPage(albuns));
    }


    @Operation(summary = "Criar álbum com upload de capas",
            description = "Cria álbum e envia capas diretamente para o MinIO.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Álbum criado",
                    content = @Content(schema = @Schema(implementation = AlbumResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Dados inválidos")
    })
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE )
    public ResponseEntity<AlbumResponseDTO> criarComCapas(
            @Parameter(description = "Título do álbum", required = true)
            @RequestParam String titulo,
            @Parameter(description = "Ano de lançamento do álbum")
            @RequestParam(required = false) Integer anoLancamento,
            @Parameter(description = "IDs dos artistas vinculados", required = true)
            @RequestParam Set<Long> artistasIds,
            @Parameter(description = "Arquivos de capa (image/*)")
            @RequestParam(name = "files", required = false) List<MultipartFile> files,
            @Parameter(description = "Define a primeira capa como principal")
            @RequestParam(defaultValue = "false") boolean principal
    ) {
        Album album = new Album();
        album.setTitulo(titulo);
        album.setAnoLancamento(anoLancamento);

        Album criado = albumService.criarComCapas(album, artistasIds, files, principal);
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(criado));
    }

    @Operation(summary = "Atualizar álbum",
            description = "Atualiza dados básicos, artistas e capas do álbum.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Álbum atualizado",
                    content = @Content(schema = @Schema(implementation = AlbumResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "404", description = "Álbum não encontrado")
    })
    @PutMapping("/{id}")
    public ResponseEntity<AlbumResponseDTO> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody AlbumRequestDTO request
    ) {
        Album album = new Album();
        album.setTitulo(request.titulo());
        album.setAnoLancamento(request.anoLancamento());

        Album atualizado = albumService.atualizar(id, album, request.artistasIds(), request.capas());
        return ResponseEntity.ok(toResponse(atualizado));
    }

    @Operation(summary = "Inativar álbum", description = "Marca o álbum como inativo.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Álbum inativado"),
            @ApiResponse(responseCode = "404", description = "Álbum não encontrado")
    })
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