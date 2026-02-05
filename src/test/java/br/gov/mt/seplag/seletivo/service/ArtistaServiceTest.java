package br.gov.mt.seplag.seletivo.service;

import br.gov.mt.seplag.seletivo.domain.entity.Artista;
import br.gov.mt.seplag.seletivo.domain.enums.TipoArtistaEnum;
import br.gov.mt.seplag.seletivo.domain.repository.ArtistaRepository;
import br.gov.mt.seplag.seletivo.exception.BusinessException;
import br.gov.mt.seplag.seletivo.exception.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ArtistaServiceTest {

    @Mock
    private ArtistaRepository repository;

    @InjectMocks
    private ArtistaService artistaService;

    @Captor
    private ArgumentCaptor<Artista> artistaCaptor;

    @Test
    void criarDevePersistirComDatasEAtivo() {
        Artista artista = new Artista();
        artista.setNome("Guns N' Roses");
        artista.setTipo(TipoArtistaEnum.BANDA);

        Artista salvo = new Artista();
        salvo.setId(10L);
        when(repository.save(any(Artista.class))).thenReturn(salvo);

        Artista resultado = artistaService.criar(artista);

        assertThat(resultado.getId()).isEqualTo(10L);
        verify(repository).save(artistaCaptor.capture());
        Artista persisted = artistaCaptor.getValue();
        assertThat(persisted.getCreatedAt()).isNotNull();
        assertThat(persisted.getUpdatedAt()).isNotNull();
        assertThat(persisted.getAtivo()).isTrue();
    }

    @Test
    void criarSemNomeDeveLancarExcecao() {
        Artista artista = new Artista();
        artista.setTipo(TipoArtistaEnum.SOLO);

        assertThatThrownBy(() -> artistaService.criar(artista))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Nome do artista é obrigatório");
    }

    @Test
    void listarPorNomeComOrdenacaoDesc() {
        Artista artista = new Artista();
        artista.setId(1L);
        when(repository.findByNomeContainingIgnoreCaseAndAtivoTrue(
                eq("Mike"), any(Sort.class)
        )).thenReturn(List.of(artista));

        List<Artista> resultado = artistaService.listar("Mike", "desc");

        assertThat(resultado).containsExactly(artista);
    }

    @Test
    void inativarDeveAtualizarRegistro() {
        Artista artista = new Artista();
        artista.setId(5L);
        artista.setAtivo(true);
        artista.setUpdatedAt(LocalDateTime.now().minusDays(1));

        when(repository.findById(5L)).thenReturn(Optional.of(artista));

        artistaService.inativar(5L);

        assertThat(artista.getAtivo()).isFalse();
        verify(repository).save(artista);
    }

    @Test
    void buscarPorIdInexistenteDeveLancarExcecao() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> artistaService.buscarPorId(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Artista não encontrado");
    }
}