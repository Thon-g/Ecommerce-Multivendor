package com.abs.app.application.admin.usermanager.query;

import com.abs.app.application.admin.usermanager.dto.UserDetailResponseDto;
import com.abs.app.common.constant.UserConstant;
import com.abs.app.common.exception.ResourceNotFoundException;
import com.abs.app.domain.entity.enums.RoleUser;
import com.abs.app.domain.entity.Order;
import com.abs.app.domain.entity.User;
import com.abs.app.domain.repository.OrderRepository;
import com.abs.app.domain.repository.UserRepository;
import com.abs.app.infrastructure.mapper.UserAdminMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GetUserDetailQueryHandler {

    private final UserRepository userRepository;
    private final OrderRepository orderRepository;

    public UserDetailResponseDto handle(GetUserDetailQuery query) {
        User user = userRepository.findById(query.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException(UserConstant.USER_NOT_EXIST));

        boolean isAdmin = user.getRoles() != null && user.getRoles().stream()
                .anyMatch(r -> r.getRoleName() == RoleUser.ADMIN);

        if (isAdmin) {
            throw new ResourceNotFoundException(UserConstant.USER_NOT_EXIST);
        }

        List<Order> orders = orderRepository.findByUserId(query.getUserId());

        return UserAdminMapper.toUserDetailResponseDto(user, orders);
    }
}
