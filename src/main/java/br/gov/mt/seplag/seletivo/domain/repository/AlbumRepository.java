package br.gov.mt.seplag.seletivo.domain.repository;

import br.gov.mt.seplag.seletivo.domain.entity.Album;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;
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
    Optional<Album> findByIdAndAtivoTrue(Long id);


}
