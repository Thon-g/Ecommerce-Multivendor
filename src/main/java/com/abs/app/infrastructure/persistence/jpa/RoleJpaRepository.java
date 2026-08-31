package com.abs.app.infrastructure.persistence.jpa;

import com.abs.app.domain.entity.Role;
import com.abs.app.domain.entity.enums.RoleUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleJpaRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByRoleName(RoleUser name);

    boolean existsByRoleName(RoleUser roleName);
}
