package br.gov.mt.seplag.seletivo.service;

import br.gov.mt.seplag.seletivo.domain.entity.Album;
import br.gov.mt.seplag.seletivo.domain.entity.Artista;
import br.gov.mt.seplag.seletivo.domain.enums.TipoArtistaEnum;
import br.gov.mt.seplag.seletivo.domain.repository.AlbumRepository;
import br.gov.mt.seplag.seletivo.domain.repository.ArtistaRepository;
import br.gov.mt.seplag.seletivo.dto.AlbumCapaRequestDTO;
import br.gov.mt.seplag.seletivo.exception.ResourceNotFoundException;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AlbumServiceTest {

    @Mock
    private AlbumRepository albumRepository;

    @Mock
    private ArtistaRepository artistaRepository;

    @Mock
    private AlbumCapaService albumCapaService;

    @Mock
    private MinioStorageService minioStorageService;

    @Mock
    private AlbumNotificationService albumNotificationService;

    @Mock
    private EntityManager entityManager;


    @InjectMocks
    private AlbumService albumService;

    @Captor
    private ArgumentCaptor<Album> albumCaptor;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(albumService, "entityManager", entityManager);
    }


    @Test
    void listarPorNomeArtistaEmBrancoDeveRetornarTodos() {
        Page<Album> esperado = new PageImpl<>(List.of(new Album()));
        when(albumRepository.findByAtivoTrue(any(Pageable.class))).thenReturn(esperado);

        Page<Album> resultado = albumService.listarPorNomeArtista("  ", PageRequest.of(0, 10));

        assertThat(resultado).isSameAs(esperado);
        verify(albumRepository).findByAtivoTrue(any(Pageable.class));
        verify(albumRepository, never())
                .findByArtistasNomeContainingIgnoreCaseAndAtivoTrue(any(), any());
    }

    @Test
    void listarPorNomeArtistaSemResultadosDeveLancarExcecao() {
        when(albumRepository.findByArtistasNomeContainingIgnoreCaseAndAtivoTrue(
                eq("Guns"), any(Pageable.class)
        )).thenReturn(Page.empty());

        assertThatThrownBy(() -> albumService.listarPorNomeArtista("Guns", PageRequest.of(0, 5)))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Nenhum álbum vinculado a este artista");
    }

    @Test
    void atualizarDeveSubstituirCapasEArtistas() {
        Album existente = new Album();
        existente.setId(2L);
        existente.setTitulo("Old");
        existente.setUpdatedAt(LocalDateTime.now().minusDays(1));

        Artista artistaAtual = new Artista();
        artistaAtual.setId(1L);

        artistaAtual.setAlbuns(new HashSet<>(Set.of(existente)));
        existente.setArtistas(new HashSet<>(Set.of(artistaAtual)));

        when(albumRepository.findByIdAndAtivoTrue(2L)).thenReturn(Optional.of(existente));
        Artista novoArtista = new Artista();
        novoArtista.setId(3L);
        when(artistaRepository.findAllById(Set.of(3L))).thenReturn(List.of(novoArtista));

        Album atualizado = new Album();
        atualizado.setTitulo("Novo");
        atualizado.setAnoLancamento(2024);

        List<AlbumCapaRequestDTO> novasCapas = List.of(new AlbumCapaRequestDTO("nova", true));

        albumService.atualizar(2L, atualizado, Set.of(3L), novasCapas);

        verify(albumCapaService).removerTodasPorAlbum(2L);
        verify(albumCapaService).adicionarCapa(2L, "nova", true);
        verify(albumRepository).save(existente);

        assertThat(existente.getTitulo()).isEqualTo("Novo");
        assertThat(existente.getAnoLancamento()).isEqualTo(2024);
        assertThat(existente.getArtistas()).containsExactly(novoArtista);
        assertThat(artistaAtual.getAlbuns()).doesNotContain(existente);
    }

    @Test
    void listarPorFiltrosDeveDelegarParaRepositorio() {
        Page<Album> esperado = new PageImpl<>(List.of(new Album()));
        when(albumRepository.findByFiltros(eq("Titulo"), eq("Artista"), eq(TipoArtistaEnum.BANDA), any(Pageable.class)))
                .thenReturn(esperado);

        Page<Album> resultado = albumService.listarPorFiltros("Titulo", "Artista", TipoArtistaEnum.BANDA, PageRequest.of(0, 10));

        assertThat(resultado).isSameAs(esperado);
        verify(albumRepository).findByFiltros(eq("Titulo"), eq("Artista"), eq(TipoArtistaEnum.BANDA), any(Pageable.class));
    }
}
