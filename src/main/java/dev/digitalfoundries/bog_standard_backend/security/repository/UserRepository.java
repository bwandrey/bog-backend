package dev.digitalfoundries.bog_standard_backend.security.repository;

import dev.digitalfoundries.bog_standard_backend.security.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findByUsername(String username);
}