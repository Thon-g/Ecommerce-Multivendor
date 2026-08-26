package com.abs.app.application.auth.command;

import com.abs.app.application.auth.dto.AuthResponseDto;
import com.abs.app.common.constant.AuthConstant;
import com.abs.app.common.constant.RoleConstant;
import com.abs.app.common.exception.DuplicateResourceException;
import com.abs.app.common.exception.ResourceNotFoundException;
import com.abs.app.common.util.GenerateIdUtil;
import com.abs.app.domain.entity.Cart;
import com.abs.app.domain.entity.Role;
import com.abs.app.domain.entity.User;
import com.abs.app.domain.entity.enums.RoleUser;
import com.abs.app.domain.repository.CartRepository;
import com.abs.app.domain.repository.RoleRepository;
import com.abs.app.domain.repository.UserRepository;
import com.abs.app.infrastructure.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class RegisterUserCommandHandler {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final CartRepository cartRepository ;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    public AuthResponseDto handle(RegisterUserCommand command) {
        if(userRepository.existsByEmail(command.getEmail())) {
            throw new DuplicateResourceException(AuthConstant.EMAIL_EXIST);
        }

        Role role = roleRepository.findByRoleName(RoleUser.CUSTOMER)
                .orElseThrow(() -> new ResourceNotFoundException(RoleConstant.ROLE_NOT_EXIST));

        User newUser = new User();
        newUser.setUserId(GenerateIdUtil.GenerateId());
        newUser.setUserName(command.getFirstName() + command.getLastName());
        newUser.setEmail(command.getEmail());
        newUser.setPassword(passwordEncoder.encode(command.getPassword()));
        newUser.setFirstName(command.getFirstName());
        newUser.setLastName(command.getLastName());
        newUser.setUpdateAt(LocalDateTime.now());
        newUser.setRole(role);
        userRepository.save(newUser);

        Cart newCart = new Cart();
        newCart.setUser(newUser);
        cartRepository.save(newCart);

        String accessToken = jwtTokenProvider.generateAccessToken(newUser.getUserId(), newUser.getRole().getRoleName().toString());

        return AuthResponseDto.builder()
                .accessToken(accessToken)
                .build();
    }
}
