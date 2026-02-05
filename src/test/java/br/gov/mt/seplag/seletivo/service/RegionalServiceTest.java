package br.gov.mt.seplag.seletivo.service;

import br.gov.mt.seplag.seletivo.domain.entity.Regional;
import br.gov.mt.seplag.seletivo.domain.repository.RegionalRepository;
import br.gov.mt.seplag.seletivo.dto.RegionalApiRequestDTO;
import br.gov.mt.seplag.seletivo.dto.RegionalResponseDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RegionalServiceTest {

    @Mock
    private RegionalRepository repository;

    @Mock
    private RestTemplate restTemplate;

    @Captor
    private ArgumentCaptor<Regional> regionalCaptor;

    @Test
    void listarAtivasDeveMapearResposta() {
        Regional regional = new Regional(1L, 99, "Norte", true);
        when(repository.findByAtivoTrue()).thenReturn(List.of(regional));

        RegionalService service = new RegionalService(repository, "http://localhost/mock");
        List<RegionalResponseDTO> resultado = service.listarAtivas();

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).id()).isEqualTo(1L);
        assertThat(resultado.get(0).idRegionalExterno()).isEqualTo(99);
        assertThat(resultado.get(0).nome()).isEqualTo("Norte");
        assertThat(resultado.get(0).ativo()).isTrue();
    }

    @Test
    @SuppressWarnings("unchecked")
    void sincronizarDeveInativarAusentesEAtualizarAlteradas() {
        RegionalService service = new RegionalService(repository, "http://localhost/mock");
        ReflectionTestUtils.setField(service, "restTemplate", restTemplate);

        List<RegionalApiRequestDTO> apiList = List.of(
                new RegionalApiRequestDTO(1, "Norte"),
                new RegionalApiRequestDTO(2, "Sul Novo"),
                new RegionalApiRequestDTO(3, "Leste")
        );
        when(restTemplate.exchange(
                eq("http://localhost/mock"),
                eq(HttpMethod.GET),
                eq(null),
                any(ParameterizedTypeReference.class)
        )).thenReturn(ResponseEntity.ok(apiList));

        Regional ativa1 = new Regional(10L, 1, "Norte", true);
        Regional ativa2 = new Regional(11L, 2, "Sul", true);
        Regional ativa3 = new Regional(12L, 4, "Oeste", true);
        when(repository.findByAtivoTrue()).thenReturn(List.of(ativa1, ativa2, ativa3));

        when(repository.findByIdRegionalExternoAndNomeAndAtivoTrue(1, "Norte"))
                .thenReturn(Optional.of(ativa1));
        when(repository.findByIdRegionalExternoAndNomeAndAtivoTrue(2, "Sul Novo"))
                .thenReturn(Optional.empty());
        when(repository.findByIdRegionalExternoAndNomeAndAtivoTrue(3, "Leste"))
                .thenReturn(Optional.empty());

        when(repository.findByIdRegionalExternoAndAtivoTrue(2))
                .thenReturn(List.of(ativa2));
        when(repository.findByIdRegionalExternoAndAtivoTrue(3))
                .thenReturn(List.of());

        service.sincronizar();

        verify(repository, times(5)).save(regionalCaptor.capture());
        List<Regional> salvos = regionalCaptor.getAllValues();

        assertThat(salvos)
                .anyMatch(regional -> regional.getIdRegionalExterno().equals(2)
                        && regional.getNome().equals("Sul")
                        && !regional.getAtivo())
                .anyMatch(regional -> regional.getIdRegionalExterno().equals(4)
                        && regional.getNome().equals("Oeste")
                        && !regional.getAtivo())
                .anyMatch(regional -> regional.getIdRegionalExterno().equals(2)
                        && regional.getNome().equals("Sul Novo")
                        && regional.getAtivo())
                .anyMatch(regional -> regional.getIdRegionalExterno().equals(3)
                        && regional.getNome().equals("Leste")
                        && regional.getAtivo());
    }
}