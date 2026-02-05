package br.gov.mt.seplag.seletivo.domain.repository;

import br.gov.mt.seplag.seletivo.domain.entity.Album;
import br.gov.mt.seplag.seletivo.domain.enums.TipoArtistaEnum;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AlbumRepository extends JpaRepository<Album, Long> {


    @EntityGraph(attributePaths = {"artistas", "capas"})
    Page<Album> findByAtivoTrue(Pageable pageable);

    @EntityGraph(attributePaths = {"artistas", "capas"})
    Page<Album> findByArtistasIdAndAtivoTrue(Long artistaId, Pageable pageable);

    @EntityGraph(attributePaths = {"artistas", "capas"})
    Page<Album> findByArtistasNomeContainingIgnoreCaseAndAtivoTrue(String nome, Pageable pageable);

    @EntityGraph(attributePaths = {"artistas", "capas"})
    Page<Album> findByArtistasTipoAndAtivoTrue(TipoArtistaEnum tipo, Pageable pageable);

    @EntityGraph(attributePaths = {"artistas", "capas"})
    Optional<Album> findByIdAndAtivoTrue(Long id);

    @Query("""
            SELECT DISTINCT a FROM Album a
            JOIN a.artistas ar
            WHERE a.ativo = true
              AND (:titulo IS NULL OR LOWER(a.titulo) LIKE LOWER(CONCAT('%', :titulo, '%')))
              AND (:artistaNome IS NULL OR LOWER(ar.nome) LIKE LOWER(CONCAT('%', :artistaNome, '%')))
              AND (:tipo IS NULL OR ar.tipo = :tipo)
            """)
    Page<Album> findByFiltros(
            @Param("titulo") String titulo,
            @Param("artistaNome") String artistaNome,
            @Param("tipo") TipoArtistaEnum tipo,
            Pageable pageable
    );
}
