package com.abs.app.application.user.address.query;

import com.abs.app.application.user.address.dto.AddressResponseDto;
import com.abs.app.common.constant.UserConstant;
import com.abs.app.common.exception.ResourceNotFoundException;
import com.abs.app.domain.entity.User;
import com.abs.app.domain.repository.UserRepository;
import com.abs.app.infrastructure.mapper.AddressMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GetUserAddressesQueryHandler {
    private final UserRepository userRepository;

    public List<AddressResponseDto> handle(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(UserConstant.USER_NOT_EXIST));

        return user.getAddresses().stream()
                .map(AddressMapper::toAddressResponseDto)
                .collect(Collectors.toList());
    }
}
