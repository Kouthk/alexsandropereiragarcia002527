package br.gov.mt.seplag.seletivo.domain.repository;

import br.gov.mt.seplag.seletivo.domain.entity.Artista;
import br.gov.mt.seplag.seletivo.domain.enums.TipoArtistaEnum;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ArtistaRepository extends JpaRepository<Artista, Long> {


    List<Artista> findByAtivoTrue(Sort sort);

    List<Artista> findByNomeContainingIgnoreCaseAndAtivoTrue(String nome, Sort sort);

    List<Artista> findByTipoAndAtivoTrue(TipoArtistaEnum tipo);

}
