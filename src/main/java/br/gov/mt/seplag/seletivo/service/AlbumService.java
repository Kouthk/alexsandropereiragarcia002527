package br.gov.mt.seplag.seletivo.service;

import br.gov.mt.seplag.seletivo.domain.entity.Album;
import br.gov.mt.seplag.seletivo.domain.entity.Artista;
import br.gov.mt.seplag.seletivo.domain.enums.TipoArtistaEnum;
import br.gov.mt.seplag.seletivo.domain.repository.AlbumRepository;
import br.gov.mt.seplag.seletivo.domain.repository.ArtistaRepository;
import br.gov.mt.seplag.seletivo.dto.AlbumCapaRequestDTO;
import br.gov.mt.seplag.seletivo.exception.ResourceNotFoundException;
import br.gov.mt.seplag.seletivo.exception.enums.LayerEnum;
import br.gov.mt.seplag.seletivo.exception.LayerDefinition;
import jakarta.validation.constraints.Min;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


@Service
public class AlbumService implements LayerDefinition {

    private final AlbumRepository albumRepository;
    private final ArtistaRepository artistaRepository;
    private final AlbumCapaService albumCapaService;
    private final MinioStorageService minioStorageService;
    private final AlbumNotificationService albumNotificationService;

    public AlbumService(
            AlbumRepository albumRepository,
            ArtistaRepository artistaRepository,
            AlbumCapaService albumCapaService,
            MinioStorageService minioStorageService,
            AlbumNotificationService albumNotificationService
    ) {
        this.albumRepository = albumRepository;
        this.artistaRepository = artistaRepository;
        this.albumCapaService = albumCapaService;
        this.minioStorageService = minioStorageService;
        this.albumNotificationService = albumNotificationService;
    }

    /**
     * Cria um álbum e associa aos artistas informados.
     */
    @Transactional
    public Album criar(Album album, Set<Long> artistasIds, List<AlbumCapaRequestDTO> capas) {
        album.setId(null);
        album.setCreatedAt(LocalDateTime.now());
        album.setUpdatedAt(LocalDateTime.now());

        Set<Artista> artistas = buscarArtistasObrigatorios(artistasIds);
        vincularArtistas(album, artistas);

        Album salvo = albumRepository.save(album);
        if (capas != null && !capas.isEmpty()) {
            for (AlbumCapaRequestDTO capa : capas) {
                albumCapaService.adicionarCapa(
                        salvo.getId(),
                        capa.objectKey(),
                        Boolean.TRUE.equals(capa.principal())
                );
            }
        }
        Album carregado = albumRepository.findByIdAndAtivoTrue(salvo.getId())
                .orElse(salvo);
        inicializarRelacoes(carregado);
        albumNotificationService.notifyAlbumCreated(carregado);
        return carregado;
    }

    /**
     * Cria Album com Capa
     */
    @Transactional
    public Album criarComCapas(
            Album album,
            Set<Long> artistasIds,
            List<MultipartFile> files,
            boolean principal
    ) {
        album.setId(null);
        album.setCreatedAt(LocalDateTime.now());
        album.setUpdatedAt(LocalDateTime.now());

        Set<Artista> artistas = buscarArtistasObrigatorios(artistasIds);
        vincularArtistas(album, artistas);

        // 1) salva primeiro pra ter ID
        Album salvo = albumRepository.save(album);

        // 2) sobe as capas no MinIO usando o ID do álbum
        if (files != null && !files.isEmpty()) {
            List<String> objectKeys = minioStorageService.uploadAlbumCapas(files);
            List<AlbumCapaRequestDTO> capas = buildCapas(objectKeys, principal);

            // 3) persiste as capas no banco (e marca principal)
            for (AlbumCapaRequestDTO capa : capas) {
                albumCapaService.adicionarCapa(
                        salvo.getId(),
                        capa.objectKey(),
                        Boolean.TRUE.equals(capa.principal())
                );
            }
        }
        Album carregado = albumRepository.findByIdAndAtivoTrue(salvo.getId())
                .orElse(salvo);
        inicializarRelacoes(carregado);
        albumNotificationService.notifyAlbumCreated(carregado);
        return carregado;
    }

    /**
     * Busca álbum por ID.
     */
    @Transactional(readOnly = true)
    public Album buscarPorId(Long id) {
        Album album = albumRepository.findByIdAndAtivoTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Álbum não encontrado",
                        this
                ));
        inicializarRelacoes(album);
        return album;
    }

    /**
     * Lista todos os álbuns com paginação.
     */
    @Transactional(readOnly = true)
    public Page<Album> listar(Pageable pageable) {
        Page<Album> page = albumRepository.findByAtivoTrue(pageable);
        inicializarRelacoes(page.getContent());
        return page;
    }

    /**
     * Lista álbuns vinculados a um artista específico.
     */
    @Transactional(readOnly = true)
    public Page<Album> listarPorArtista(Long artistaId, Pageable pageable) {
        Page<Album> resultado = albumRepository.findByArtistasIdAndAtivoTrue(artistaId, pageable);
        validarResultadoArtista(resultado);
        inicializarRelacoes(resultado.getContent());
        return resultado;
    }

    /**
     * Lista álbuns por nome do artista.
     */
    @Transactional(readOnly = true)
    public Page<Album> listarPorNomeArtista(String nome, Pageable pageable) {
        if (nome == null || nome.isBlank()) {
            return albumRepository.findByAtivoTrue(pageable);
        }
        Page<Album> resultado = albumRepository.findByArtistasNomeContainingIgnoreCaseAndAtivoTrue(nome, pageable);
        validarResultadoArtista(resultado);
        return resultado;
    }

    /**
     * Lista álbuns por tipo de artista (BANDA ou SOLO).
     */
    @Transactional(readOnly = true)
    public Page<Album> listarPorTipoArtista(TipoArtistaEnum tipo, Pageable pageable) {
        if (tipo == null) {
            Page<Album> page = albumRepository.findByAtivoTrue(pageable);
            inicializarRelacoes(page.getContent());
            return page;
        }
        Page<Album> resultado = albumRepository.findByArtistasTipoAndAtivoTrue(tipo, pageable);
        validarResultadoArtista(resultado);
        inicializarRelacoes(resultado.getContent());
        return resultado;
    }

    /**
     * Lista álbuns por filtros combinados: título do álbum, nome do artista e tipo.
     */
    @Transactional(readOnly = true)
    public Page<Album> listarPorFiltros(String titulo, String artistaNome, TipoArtistaEnum tipo, Pageable pageable) {
        Page<Album> page = albumRepository.findByFiltros(
                regularizaFiltro(titulo),
                regularizaFiltro(artistaNome),
                tipo,
                pageable
        );
        inicializarRelacoes(page.getContent());
        return page;
    }


    /**
     * Atualiza dados básicos do álbum.
     */
    @Transactional
    public Album atualizar(
            Long id,
            Album atualizado,
            Set<Long> artistasIds,
            List<AlbumCapaRequestDTO> capas
    ) {
        Album existente = buscarPorId(id);

        existente.setTitulo(atualizado.getTitulo());
        existente.setAnoLancamento(atualizado.getAnoLancamento());
        existente.setUpdatedAt(LocalDateTime.now());

        if (artistasIds != null) {
            Set<Artista> artistas = buscarArtistasObrigatorios(artistasIds);
            atualizarArtistas(existente, artistas);
        }

        if (capas != null) {
            albumCapaService.removerTodasPorAlbum(existente.getId());
            for (AlbumCapaRequestDTO capa : capas) {
                albumCapaService.adicionarCapa(
                        existente.getId(),
                        capa.objectKey(),
                        Boolean.TRUE.equals(capa.principal())
                );
            }
        }

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

    private List<AlbumCapaRequestDTO> buildCapas(List<String> objectKeys, boolean principal) {
        if (objectKeys == null || objectKeys.isEmpty()) {
            return List.of();
        }

        List<AlbumCapaRequestDTO> capas = new java.util.ArrayList<>();
        for (int index = 0; index < objectKeys.size(); index++) {
            boolean isPrincipal = principal && index == 0;
            capas.add(new AlbumCapaRequestDTO(objectKeys.get(index), isPrincipal));
        }
        return capas;
    }

    private void validarResultadoArtista(Page<Album> resultado) {
        if (resultado.isEmpty()) {
            throw new ResourceNotFoundException("Nenhum álbum vinculado a este artista", this);
        }
    }

    private void vincularArtistas(Album album, Set<Artista> artistas) {
        album.setArtistas(artistas);
        for (Artista artista : artistas) {
            Set<Album> albuns = artista.getAlbuns();
            if (albuns == null) {
                albuns = new HashSet<>();
                artista.setAlbuns(albuns);
            }
            albuns.add(album);
        }
    }

    private void atualizarArtistas(Album album, Set<Artista> novosArtistas) {
        Set<Artista> atuais = album.getArtistas();
        if (atuais != null) {
            for (Artista artista : atuais) {
                Set<Album> albuns = artista.getAlbuns();
                if (albuns != null) {
                    albuns.remove(album);
                }
            }
        }
        vincularArtistas(album, novosArtistas);
    }

    private String regularizaFiltro(String valor) {
        if (valor == null || valor.isBlank()) {
            return null;
        }
        return valor.trim();
    }

    private void inicializarRelacoes(Album album) {
        if (album == null) {
            return;
        }
        if (album.getArtistas() != null) {
            album.getArtistas().size();
        }
        if (album.getCapas() != null) {
            album.getCapas().size();
        }
    }

    private void inicializarRelacoes(List<Album> albums) {
        if (albums == null || albums.isEmpty()) {
            return;
        }
        for (Album album : albums) {
            inicializarRelacoes(album);
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
