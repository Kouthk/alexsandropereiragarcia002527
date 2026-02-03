package br.gov.mt.seplag.seletivo.domain.repository;

import br.gov.mt.seplag.seletivo.domain.entity.Regional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RegionalRepository extends JpaRepository<Regional, Long> {

    List<Regional> findByAtivoTrue();

    List<Regional> findByIdRegionalExternoAndAtivoTrue(Integer idRegionalExterno);

    Optional<Regional> findByIdRegionalExternoAndNomeAndAtivoTrue(Integer idRegionalExterno, String nome);
}