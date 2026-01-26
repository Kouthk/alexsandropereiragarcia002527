package br.gov.mt.seplag.seletivo.domain.repository;

import br.gov.mt.seplag.seletivo.domain.entity.AlbumCapa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface AlbumCapaRepository extends JpaRepository<AlbumCapa, Long> {

    List<AlbumCapa> findByAlbumId(Long albumId);

    Optional<AlbumCapa> findByAlbumIdAndPrincipalTrue(Long albumId);

    @Modifying
    @Query("""
        update AlbumCapa c
        set c.principal = false
        where c.album.id = :albumId
    """)
    void desmarcarPrincipal(Long albumId);

    void deleteByAlbumId(Long albumId);


}