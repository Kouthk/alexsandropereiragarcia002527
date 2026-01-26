package br.gov.mt.seplag.seletivo.domain.repository;

import br.gov.mt.seplag.seletivo.domain.entity.Album;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AlbumRepository extends JpaRepository<Album, Long> {

    Page<Album> findByAtivoTrue(Pageable pageable);

    Page<Album> findByArtistasIdAndAtivoTrue(Long artistaId, Pageable pageable);
}
