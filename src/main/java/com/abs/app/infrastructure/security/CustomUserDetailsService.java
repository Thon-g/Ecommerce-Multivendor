package com.abs.app.infrastructure.security;

import com.abs.app.domain.entity.User;
import com.abs.app.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String userId) { // userId thực sự
        User user = userRepository.findById(userId) // Sử dụng fetch join
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return new CustomUserPrincipal(user); // Sử dụng custom principal
    }
}
