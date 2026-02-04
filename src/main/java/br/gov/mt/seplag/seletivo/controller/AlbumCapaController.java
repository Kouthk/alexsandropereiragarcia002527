package br.gov.mt.seplag.seletivo.controller;

import br.gov.mt.seplag.seletivo.domain.entity.AlbumCapa;
import br.gov.mt.seplag.seletivo.dto.AlbumCapaResponseDTO;
import br.gov.mt.seplag.seletivo.service.AlbumCapaService;
import br.gov.mt.seplag.seletivo.service.MinioStorageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Tag(name = "Capas de Álbum", description = "Operações para upload e gerenciamento de capas")
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

    @Operation(summary = "Listar capas do álbum")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de capas",
                    content = @Content(schema = @Schema(implementation = AlbumCapaResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Álbum não encontrado")
    })
    @GetMapping
    public ResponseEntity<List<AlbumCapaResponseDTO>> listar(@PathVariable Long albumId) {
        List<AlbumCapaResponseDTO> capas = albumCapaService.listarPorAlbum(albumId)
                .stream()
                .map(this::toResponse)
                .toList();
        return ResponseEntity.ok(capas);
    }

    @Operation(summary = "Upload de capa",
            description = "Envia um arquivo de imagem e registra a capa para o álbum.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Capa cadastrada",
                    content = @Content(schema = @Schema(implementation = AlbumCapaResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "404", description = "Álbum não encontrado")
    })
    @PostMapping(value="/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE )
    public ResponseEntity<AlbumCapaResponseDTO> upload(
            @PathVariable Long albumId,
            @Parameter(description = "Arquivo da capa (image/*)", required = true)
            @RequestParam("file") MultipartFile file,
            @Parameter(description = "Define esta capa como principal")
            @RequestParam(defaultValue = "false") boolean principal
    ) {
        String objectKey = minioStorageService.uploadAlbumCapa(file);
        AlbumCapa capa = albumCapaService.adicionarCapa(albumId, objectKey, principal);
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(capa));
    }

    @Operation(summary = "Definir capa principal")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Capa definida como principal"),
            @ApiResponse(responseCode = "404", description = "Capa não encontrada")
    })
    @PutMapping("/{capaId}/principal")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void definirPrincipal(@PathVariable Long albumId, @PathVariable Long capaId) {
        albumCapaService.definirComoPrincipal(capaId);
    }

    @Operation(summary = "Remover capa")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Capa removida"),
            @ApiResponse(responseCode = "404", description = "Capa não encontrada")
    })
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