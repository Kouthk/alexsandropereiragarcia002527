package br.gov.mt.seplag.seletivo.security.repository;

import br.gov.mt.seplag.seletivo.security.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {
}
