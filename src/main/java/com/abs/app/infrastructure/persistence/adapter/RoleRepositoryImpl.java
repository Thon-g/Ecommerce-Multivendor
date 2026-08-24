package com.abs.app.infrastructure.persistence.adapter;

import com.abs.app.domain.entity.Role;
import com.abs.app.domain.entity.enums.RoleUser;
import com.abs.app.domain.repository.RoleRepository;
import com.abs.app.infrastructure.persistence.jpa.RoleJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RoleRepositoryImpl implements RoleRepository {
    private final RoleJpaRepository roleJpaRepository;

    @Override
    public Optional<Role> findByRoleName(RoleUser name) {
        return roleJpaRepository.findByRoleName(name);
    }

    @Override
    public boolean existsByRoleName(RoleUser roleName) {
        return roleJpaRepository.findByRoleName(roleName).isPresent();
    }

    @Override
    public void save(Role role) {
        roleJpaRepository.save(role);
    }
}
