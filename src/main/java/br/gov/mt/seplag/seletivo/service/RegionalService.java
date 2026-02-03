package br.gov.mt.seplag.seletivo.service;

import br.gov.mt.seplag.seletivo.domain.entity.Regional;
import br.gov.mt.seplag.seletivo.domain.repository.RegionalRepository;
import br.gov.mt.seplag.seletivo.dto.RegionalApiRequestDTO;
import br.gov.mt.seplag.seletivo.dto.RegionalResponseDTO;
import br.gov.mt.seplag.seletivo.exception.LayerDefinition;
import br.gov.mt.seplag.seletivo.exception.enums.LayerEnum;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class RegionalService implements LayerDefinition {

    private final RegionalRepository repository;
    private final RestTemplate restTemplate;
    private final String regionaisUrl;

    public RegionalService(
            RegionalRepository repository,
            @Value("${regionais.url}") String regionaisUrl
    ) {
        this.repository = repository;
        this.regionaisUrl = regionaisUrl;
        this.restTemplate = new RestTemplate();
    }

    @Transactional(readOnly = true)
    public List<RegionalResponseDTO> listarAtivas() {
        return repository.findByAtivoTrue()
                .stream()
                .map(regional -> new RegionalResponseDTO(
                        regional.getId(),
                        regional.getIdRegionalExterno(),
                        regional.getNome(),
                        regional.getAtivo()
                ))
                .toList();
    }

    @Transactional
    public void sincronizar() {
        List<RegionalApiRequestDTO> regionais = carregarRegionaisExternas();
        Map<Integer, String> apiPorId = new HashMap<>();
        for (RegionalApiRequestDTO regional : regionais) {
            if (regional.id() != null && regional.nome() != null) {
                apiPorId.put(regional.id(), regional.nome().trim());
            }
        }

        List<Regional> ativas = repository.findByAtivoTrue();
        for (Regional regional : ativas) {
            String nomeApi = apiPorId.get(regional.getIdRegionalExterno());
            if (nomeApi == null) {
                regional.setAtivo(false);
                repository.save(regional);
            } else if (!regional.getNome().equalsIgnoreCase(nomeApi)) {
                regional.setAtivo(false);
                repository.save(regional);
            }
        }

        for (Map.Entry<Integer, String> entry : apiPorId.entrySet()) {
            Integer id = entry.getKey();
            String nome = entry.getValue();

            boolean existeAtivoMesmaDenominacao = repository
                    .findByIdRegionalExternoAndNomeAndAtivoTrue(id, nome)
                    .isPresent();
            if (existeAtivoMesmaDenominacao) {
                continue;
            }

            List<Regional> ativasMesmoId = repository.findByIdRegionalExternoAndAtivoTrue(id);
            for (Regional regional : ativasMesmoId) {
                if (!regional.getNome().equalsIgnoreCase(nome)) {
                    regional.setAtivo(false);
                    repository.save(regional);
                }
            }

            Regional novaRegional = new Regional(null, id, nome, true);
            repository.save(novaRegional);
        }
    }

    private List<RegionalApiRequestDTO> carregarRegionaisExternas() {
        List<RegionalApiRequestDTO> body = restTemplate.exchange(
                regionaisUrl,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<RegionalApiRequestDTO>>() {
                }
        ).getBody();
        return body == null ? List.of() : body;
    }

    @Override
    public String getClassName() {
        return this.getClass().getSimpleName();
    }

    @Override
    public LayerEnum getLayer() {
        return LayerEnum.SERVICE;
    }
}
