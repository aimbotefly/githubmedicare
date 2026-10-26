package com.medicare.api.repositories;

import com.medicare.api.entities.AppRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AppRoleRepository extends JpaRepository<AppRole, Long> {

    Optional<AppRole> findByNom(String nom);

    boolean existsByNom(String nom);
}
