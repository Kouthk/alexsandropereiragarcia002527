package br.gov.mt.seplag.seletivo.service;

import br.gov.mt.seplag.seletivo.domain.entity.Album;
import br.gov.mt.seplag.seletivo.domain.entity.AlbumCapa;
import br.gov.mt.seplag.seletivo.domain.repository.AlbumCapaRepository;
import br.gov.mt.seplag.seletivo.domain.repository.AlbumRepository;
import br.gov.mt.seplag.seletivo.exception.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AlbumCapaServiceTest {

    @Mock
    private AlbumCapaRepository albumCapaRepository;

    @Mock
    private AlbumRepository albumRepository;

    @InjectMocks
    private AlbumCapaService albumCapaService;

    @Captor
    private ArgumentCaptor<AlbumCapa> capaCaptor;

    @Test
    void adicionarCapaPrincipalDeveDesmarcarAnterior() {
        Album album = new Album();
        album.setId(1L);

        when(albumRepository.findById(1L)).thenReturn(Optional.of(album));
        when(albumCapaRepository.save(any(AlbumCapa.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        AlbumCapa capa = albumCapaService.adicionarCapa(1L, "capa-1", true);

        verify(albumCapaRepository).desmarcarPrincipal(1L);
        verify(albumCapaRepository).save(capaCaptor.capture());
        AlbumCapa persisted = capaCaptor.getValue();
        assertThat(persisted.getAlbum()).isSameAs(album);
        assertThat(persisted.getObjectKey()).isEqualTo("capa-1");
        assertThat(persisted.getPrincipal()).isTrue();
        assertThat(capa.getCreatedAt()).isNotNull();
    }

    @Test
    void listarPorAlbumInexistenteDeveLancarExcecao() {
        when(albumRepository.existsById(2L)).thenReturn(false);

        assertThatThrownBy(() -> albumCapaService.listarPorAlbum(2L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Álbum não encontrado");
    }

    @Test
    void definirComoPrincipalDeveAtualizarCapa() {
        Album album = new Album();
        album.setId(3L);

        AlbumCapa capa = new AlbumCapa();
        capa.setId(9L);
        capa.setAlbum(album);
        capa.setPrincipal(false);

        when(albumCapaRepository.findById(9L)).thenReturn(Optional.of(capa));

        albumCapaService.definirComoPrincipal(9L);

        verify(albumCapaRepository).desmarcarPrincipal(3L);
        verify(albumCapaRepository).save(capa);
        assertThat(capa.getPrincipal()).isTrue();
    }

    @Test
    void removerTodasPorAlbumDeveApagarCapas() {
        when(albumRepository.existsById(4L)).thenReturn(true);
        List<AlbumCapa> capas = List.of(new AlbumCapa(), new AlbumCapa());
        when(albumCapaRepository.findByAlbumId(4L)).thenReturn(capas);

        albumCapaService.removerTodasPorAlbum(4L);

        verify(albumCapaRepository).deleteAll(capas);
    }

    @Test
    void removerInexistenteDeveLancarExcecao() {
        when(albumCapaRepository.findById(12L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> albumCapaService.remover(12L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Capa não encontrada");
    }
}