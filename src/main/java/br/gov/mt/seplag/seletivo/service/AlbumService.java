package br.gov.mt.seplag.seletivo.service;

import br.gov.mt.seplag.seletivo.domain.entity.Album;
import br.gov.mt.seplag.seletivo.domain.entity.Artista;
import br.gov.mt.seplag.seletivo.domain.repository.AlbumRepository;
import br.gov.mt.seplag.seletivo.domain.repository.ArtistaRepository;
import br.gov.mt.seplag.seletivo.exception.ResourceNotFoundException;
import br.gov.mt.seplag.seletivo.exception.enums.LayerEnum;
import br.gov.mt.seplag.seletivo.exception.LayerDefinition;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;


@Service
public class AlbumService implements LayerDefinition {

    private final AlbumRepository albumRepository;
    private final ArtistaRepository artistaRepository;

    public AlbumService(
            AlbumRepository albumRepository,
            ArtistaRepository artistaRepository
    ) {
        this.albumRepository = albumRepository;
        this.artistaRepository = artistaRepository;
    }

    /**
     * Cria um álbum e associa aos artistas informados.
     */
    @Transactional
    public Album criar(Album album, Set<Long> artistasIds) {
        album.setId(null);
        album.setCreatedAt(LocalDateTime.now());
        album.setUpdatedAt(LocalDateTime.now());

        Set<Artista> artistas = buscarArtistasObrigatorios(artistasIds);
        album.setArtistas(artistas);

        return albumRepository.save(album);
    }

    /**
     * Busca álbum por ID.
     */
    public Album buscarPorId(Long id) {
        return albumRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Álbum não encontrado",
                        this
                ));
    }

    /**
     * Lista todos os álbuns com paginação.
     */
    public Page<Album> listar(Pageable pageable) {
        return albumRepository.findByAtivoTrue(pageable);
    }

    /**
     * Lista álbuns vinculados a um artista específico.
     */
    public Page<Album> listarPorArtista(Long artistaId, Pageable pageable) {
        validarArtistaExiste(artistaId);
        return albumRepository.findByArtistasIdAndAtivoTrue(artistaId, pageable);
    }

    /**
     * Atualiza dados básicos do álbum.
     */
    @Transactional
    public Album atualizar(Long id, Album atualizado) {
        Album existente = buscarPorId(id);

        existente.setTitulo(atualizado.getTitulo());
        existente.setAnoLancamento(atualizado.getAnoLancamento());
        existente.setUpdatedAt(LocalDateTime.now());

        return albumRepository.save(existente);
    }

    /**
     * Remove um álbum.
     * As capas e vínculos são
     *
     * por cascade no banco.
     */
    @Transactional
    public void inativar(Long id) {
        Album album = buscarPorId(id);
        album.setAtivo(false);
        album.setUpdatedAt(LocalDateTime.now());
        albumRepository.save(album);
    }

    /**
     * Valida e retorna artistas obrigatórios para criação de álbum.
     */
    private Set<Artista> buscarArtistasObrigatorios(Set<Long> artistasIds) {
        if (artistasIds == null || artistasIds.isEmpty()) {
            throw new ResourceNotFoundException(
                    "É obrigatório informar ao menos um artista",
                    this
            );
        }

        Set<Artista> artistas = new HashSet<>(
                artistaRepository.findAllById(artistasIds)
        );

        if (artistas.size() != artistasIds.size()) {
            throw new ResourceNotFoundException(
                    "Um ou mais artistas informados não foram encontrados",
                    this
            );
        }

        return artistas;
    }

    private void validarArtistaExiste(Long artistaId) {
        if (!artistaRepository.existsById(artistaId)) {
            throw new ResourceNotFoundException("Artista não encontrado", this);
        }
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
