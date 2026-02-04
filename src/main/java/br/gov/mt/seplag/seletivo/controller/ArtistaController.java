package br.gov.mt.seplag.seletivo.controller;

import br.gov.mt.seplag.seletivo.domain.entity.Artista;
import br.gov.mt.seplag.seletivo.domain.enums.TipoArtistaEnum;
import br.gov.mt.seplag.seletivo.dto.ArtistaRequestDTO;
import br.gov.mt.seplag.seletivo.dto.ArtistaResponseDTO;
import br.gov.mt.seplag.seletivo.service.ArtistaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Artistas", description = "Operações para cadastro e consulta de artistas")
@RestController
@RequestMapping("/api/v1/artistas")
public class ArtistaController {

    private final ArtistaService artistaService;

    public ArtistaController(ArtistaService artistaService) {
        this.artistaService = artistaService;
    }

    @Operation(summary = "Listar artistas", description = "Lista artistas ativos com filtro por nome e ordenação.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de artistas",
                    content = @Content(schema = @Schema(implementation = ArtistaResponseDTO.class)))
    })
    @GetMapping
    public ResponseEntity<List<ArtistaResponseDTO>> listar(
            @Parameter(description = "Filtrar por Nome", example = "Guns")
            @RequestParam(required = false) String nome,
            @Parameter(description = "Direção de ordenação: asc ou desc", example = "asc")
            @RequestParam(defaultValue = "asc") String direcao
    ) {
        List<ArtistaResponseDTO> resposta = artistaService.listar(nome, direcao)
                .stream()
                .map(this::toResponse)
                .toList();

        return ResponseEntity.ok(resposta);
    }

    @Operation(summary = "Buscar artista por ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Artista encontrado",
                    content = @Content(schema = @Schema(implementation = ArtistaResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Artista não encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ArtistaResponseDTO> buscarPorId(@PathVariable Long id) {
        Artista artista = artistaService.buscarPorId(id);
        return ResponseEntity.ok(toResponse(artista));
    }

    @Operation(summary = "Criar artista")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Artista criado",
                    content = @Content(schema = @Schema(implementation = ArtistaResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Dados inválidos")
    })
    @PostMapping
    public ResponseEntity<ArtistaResponseDTO> criar(
            @Valid @RequestBody ArtistaRequestDTO request
    ) {
        Artista artista = new Artista();
        artista.setNome(request.nome());
        artista.setTipo(request.tipo());

        Artista criado = artistaService.criar(artista);
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(criado));
    }

    @Operation(summary = "Atualizar artista")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Artista atualizado",
                    content = @Content(schema = @Schema(implementation = ArtistaResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "404", description = "Artista não encontrado")
    })
    @PutMapping("/{id}")
    public ResponseEntity<ArtistaResponseDTO> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody ArtistaRequestDTO request
    ) {
        Artista artista = new Artista();
        artista.setNome(request.nome());
        artista.setTipo(request.tipo());

        Artista atualizado = artistaService.atualizar(id, artista);
        return ResponseEntity.ok(toResponse(atualizado));
    }

    @Operation(summary = "Inativar artista")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Artista inativado"),
            @ApiResponse(responseCode = "404", description = "Artista não encontrado")
    })
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void inativar(@PathVariable Long id) {
        artistaService.inativar(id);
    }

    private ArtistaResponseDTO toResponse(Artista artista) {
        return new ArtistaResponseDTO(
                artista.getId(),
                artista.getNome(),
                artista.getTipo(),
                artista.getAtivo()
        );
    }
}