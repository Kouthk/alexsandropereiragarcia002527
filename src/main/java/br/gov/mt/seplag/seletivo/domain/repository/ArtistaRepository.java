package br.gov.mt.seplag.seletivo.domain.repository;

import br.gov.mt.seplag.seletivo.domain.entity.Artista;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ArtistaRepository extends JpaRepository<Artista, Long> {

}
