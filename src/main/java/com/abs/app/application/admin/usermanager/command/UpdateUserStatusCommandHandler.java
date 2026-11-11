package com.abs.app.application.admin.usermanager.command;

import com.abs.app.application.admin.usermanager.dto.UserResponseDto;
import com.abs.app.common.constant.UserConstant;
import com.abs.app.common.exception.BusinessException;
import com.abs.app.common.exception.ResourceNotFoundException;
import com.abs.app.domain.entity.User;
import com.abs.app.domain.entity.enums.RoleUser;
import com.abs.app.domain.entity.enums.UserStatus;
import com.abs.app.domain.repository.UserRepository;
import com.abs.app.domain.service.SellerService;
import com.abs.app.infrastructure.mapper.UserAdminMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UpdateUserStatusCommandHandler {

    private final UserRepository userRepository;
    private final SellerService sellerService;

    public UserResponseDto handle(UpdateUserStatusCommand command) {
        User user = userRepository.findById(command.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException(UserConstant.USER_NOT_EXIST));

        boolean isAdmin = user.getRoles() != null && user.getRoles().stream()
                .anyMatch(r -> r.getRoleName() == RoleUser.ADMIN);

        if (isAdmin) {
            throw new BusinessException(UserConstant.CANNOT_BAN_ADMIN);
        }

        user.setStatus(command.getStatus());
        user.setUpdateAt(LocalDateTime.now());
        userRepository.save(user);

        sellerService.handleUserStatusChange(user.getUserId(), command.getStatus());

        return UserAdminMapper.toUserResponseDto(user);
    }
}
