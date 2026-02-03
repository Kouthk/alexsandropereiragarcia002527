package br.gov.mt.seplag.seletivo.controller;

import br.gov.mt.seplag.seletivo.dto.RegionalResponseDTO;
import br.gov.mt.seplag.seletivo.service.RegionalService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/regionais")
public class RegionalController {

    private final RegionalService regionalService;

    public RegionalController(RegionalService regionalService) {
        this.regionalService = regionalService;
    }

    @GetMapping
    public ResponseEntity<List<RegionalResponseDTO>> listar() {
        return ResponseEntity.ok(regionalService.listarAtivas());
    }

    @PostMapping("/sync")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void sincronizar() {
        regionalService.sincronizar();
    }
}