package com.abs.app.infrastructure.persistence.seeder;

import com.abs.app.domain.entity.enums.RoleUser;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.abs.app.domain.entity.Role;
import com.abs.app.domain.repository.RoleRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class RoleSeeder implements CommandLineRunner {

    private final RoleRepository roleRepository;

    @Override
    public void run(String... args) {
        seedRole(RoleUser.ADMIN);
        seedRole(RoleUser.SELLER);
        seedRole(RoleUser.CUSTOMER);
    }

    private void seedRole(RoleUser roleUser) {
        if (!roleRepository.existsByRoleName(roleUser)) {
            Role role = new Role();
            role.setRoleName(roleUser);
            roleRepository.save(role);
        }
    }
}
