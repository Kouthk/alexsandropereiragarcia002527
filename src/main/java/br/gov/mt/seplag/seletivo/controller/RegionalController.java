package br.gov.mt.seplag.seletivo.controller;

import br.gov.mt.seplag.seletivo.dto.RegionalResponseDTO;
import br.gov.mt.seplag.seletivo.service.RegionalService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Regionais", description = "Consulta e sincronização de regionais")
@RestController
@RequestMapping("/api/v1/regionais")
public class RegionalController {

    private final RegionalService regionalService;

    public RegionalController(RegionalService regionalService) {
        this.regionalService = regionalService;
    }

    @Operation(summary = "Listar regionais ativas")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de regionais",
                    content = @Content(schema = @Schema(implementation = RegionalResponseDTO.class)))
    })
    @GetMapping
    public ResponseEntity<List<RegionalResponseDTO>> listar() {
        return ResponseEntity.ok(regionalService.listarAtivas());
    }

    @Operation(summary = "Sincronizar regionais",
            description = "Importa regionais da API externa e aplica regras de ativação/inativação.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Sincronização executada")
    })
    @PostMapping("/sync")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void sincronizar() {
        regionalService.sincronizar();
    }
}