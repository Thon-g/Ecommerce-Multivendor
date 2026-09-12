package com.abs.app.domain.repository;

import com.abs.app.domain.entity.Role;
import com.abs.app.domain.entity.enums.RoleUser;

import java.util.Optional;

public interface RoleRepository {
    Optional<Role> findByRoleName(RoleUser name);

    boolean existsByRoleName(RoleUser roleName);

    void save(Role role);
}
