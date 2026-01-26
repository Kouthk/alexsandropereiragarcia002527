package br.gov.mt.seplag.seletivo.service;

import br.gov.mt.seplag.seletivo.domain.entity.Album;
import br.gov.mt.seplag.seletivo.domain.entity.AlbumCapa;
import br.gov.mt.seplag.seletivo.domain.repository.AlbumCapaRepository;
import br.gov.mt.seplag.seletivo.domain.repository.AlbumRepository;
import br.gov.mt.seplag.seletivo.exception.LayerDefinition;
import br.gov.mt.seplag.seletivo.exception.ResourceNotFoundException;
import br.gov.mt.seplag.seletivo.exception.enums.LayerEnum;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AlbumCapaService implements LayerDefinition {

    private final AlbumCapaRepository albumCapaRepository;
    private final AlbumRepository albumRepository;

    public AlbumCapaService(
            AlbumCapaRepository albumCapaRepository,
            AlbumRepository albumRepository
    ) {
        this.albumCapaRepository = albumCapaRepository;
        this.albumRepository = albumRepository;
    }

    /**
     * Adiciona uma capa ao álbum.
     * Se for principal, desmarca qualquer outra capa principal existente.
     */
    @Transactional
    public AlbumCapa adicionarCapa(
            Long albumId,
            String objectKey,
            boolean principal
    ) {
        Album album = albumRepository.findById(albumId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Álbum não encontrado",
                        this
                ));

        if (principal) {
            albumCapaRepository.desmarcarPrincipal(albumId);
        }

        AlbumCapa capa = new AlbumCapa();
        capa.setAlbum(album);
        capa.setObjectKey(objectKey);
        capa.setPrincipal(principal);
        capa.setCreatedAt(LocalDateTime.now());

        return albumCapaRepository.save(capa);
    }

    /**
     * Lista todas as capas de um álbum.
     */
    public List<AlbumCapa> listarPorAlbum(Long albumId) {
        validarAlbumExiste(albumId);
        return albumCapaRepository.findByAlbumId(albumId);
    }

    /**
     * Define uma capa específica como principal.
     */
    @Transactional
    public void definirComoPrincipal(Long capaId) {
        AlbumCapa capa = albumCapaRepository.findById(capaId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Capa não encontrada",
                        this
                ));

        albumCapaRepository.desmarcarPrincipal(capa.getAlbum().getId());
        capa.setPrincipal(true);

        albumCapaRepository.save(capa);
    }

    /**
     * Remove uma capa específica.
     */
    public void remover(Long capaId) {
        AlbumCapa capa = albumCapaRepository.findById(capaId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Capa não encontrada",
                        this
                ));

        albumCapaRepository.delete(capa);
    }

    /**
     * Remove todas as capas de um álbum (usado ao deletar álbum).
     */
    @Transactional
    public void removerTodasPorAlbum(Long albumId) {
        validarAlbumExiste(albumId);
        List<AlbumCapa> capas = albumCapaRepository.findByAlbumId(albumId);
        albumCapaRepository.deleteAll(capas);
    }

    private void validarAlbumExiste(Long albumId) {
        if (!albumRepository.existsById(albumId)) {
            throw new ResourceNotFoundException("Álbum não encontrado", this);
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
