package br.gov.mt.seplag.seletivo.controller;

import br.gov.mt.seplag.seletivo.domain.entity.Artista;
import br.gov.mt.seplag.seletivo.domain.enums.TipoArtistaEnum;
import br.gov.mt.seplag.seletivo.dto.ArtistaRequestDTO;
import br.gov.mt.seplag.seletivo.dto.ArtistaResponseDTO;
import br.gov.mt.seplag.seletivo.service.ArtistaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/artistas")
public class ArtistaController {

    private final ArtistaService artistaService;

    public ArtistaController(ArtistaService artistaService) {
        this.artistaService = artistaService;
    }

    @GetMapping
    public ResponseEntity<List<ArtistaResponseDTO>> listar(
            @RequestParam(required = false) String nome,
            @RequestParam(defaultValue = "asc") String direcao
    ) {
        List<ArtistaResponseDTO> resposta = artistaService.listar(nome, direcao)
                .stream()
                .map(this::toResponse)
                .toList();

        return ResponseEntity.ok(resposta);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ArtistaResponseDTO> buscarPorId(@PathVariable Long id) {
        Artista artista = artistaService.buscarPorId(id);
        return ResponseEntity.ok(toResponse(artista));
    }

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