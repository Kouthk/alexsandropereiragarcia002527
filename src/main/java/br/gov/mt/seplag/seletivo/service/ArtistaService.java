package br.gov.mt.seplag.seletivo.service;

import br.gov.mt.seplag.seletivo.domain.entity.Artista;
import br.gov.mt.seplag.seletivo.domain.enums.TipoArtistaEnum;
import br.gov.mt.seplag.seletivo.domain.repository.ArtistaRepository;
import br.gov.mt.seplag.seletivo.exception.BusinessException;
import br.gov.mt.seplag.seletivo.exception.LayerDefinition;
import br.gov.mt.seplag.seletivo.exception.ResourceNotFoundException;
import br.gov.mt.seplag.seletivo.exception.enums.LayerEnum;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ArtistaService implements LayerDefinition {

    private final ArtistaRepository repository;

    public ArtistaService(ArtistaRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public Artista criar(Artista artista) {
        validarArtista(artista);

        artista.setId(null);
        artista.setAtivo(true);
        artista.setCreatedAt(LocalDateTime.now());
        artista.setUpdatedAt(LocalDateTime.now());

        return repository.save(artista);
    }

    @Transactional
    public Artista atualizar(Long id, Artista artistaUpdate) {
        validarArtista(artistaUpdate);
        Artista existente = buscarPorId(id);

        existente.setNome(artistaUpdate.getNome());
        existente.setTipo(artistaUpdate.getTipo());
        existente.setUpdatedAt(LocalDateTime.now());

        return repository.save(existente);
    }

    public Artista buscarPorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Artista não encontrado", this)
                );
    }

    /**
     * Consulta por nome com ordenação asc/desc.
     * Delegado ao banco (escala melhor).
     */
    public List<Artista> listar(String nome, String direcao) {
        Sort sort = Sort.by(
                "desc".equalsIgnoreCase(direcao)
                        ? Sort.Direction.DESC
                        : Sort.Direction.ASC,
                "nome"
        );

        if (nome == null || nome.isBlank()) {
            return repository.findByAtivoTrue(sort);
        }

        return repository.findByNomeContainingIgnoreCaseAndAtivoTrue(nome, sort);
    }

    public List<Artista> listarPorTipo(TipoArtistaEnum tipo) {
        return repository.findByTipoAndAtivoTrue(tipo);
    }

    @Transactional
    public void inativar(Long id) {
        Artista artista = buscarPorId(id);
        artista.setAtivo(false);
        artista.setUpdatedAt(LocalDateTime.now());
        repository.save(artista);
    }

    private void validarArtista(Artista artista) {
        if (artista == null) {
            throw new BusinessException("Artista é obrigatório", this);
        }

        if (artista.getNome() == null || artista.getNome().isBlank()) {
            throw new BusinessException("Nome do artista é obrigatório", this);
        }

        if (artista.getTipo() == null) {
            throw new BusinessException("Tipo do artista é obrigatório", this);
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