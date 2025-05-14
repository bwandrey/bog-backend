package dev.digitalfoundries.bog_standard_backend.security.repository;

import dev.digitalfoundries.bog_standard_backend.security.entity.RoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<RoleEntity, Long> {

    Optional<RoleEntity> getByName(String roleAdmin);

    boolean existsByName(String roleAdmin);
}
