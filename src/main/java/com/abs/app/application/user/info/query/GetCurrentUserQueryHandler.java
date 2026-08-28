package com.abs.app.application.user.info.query;

import com.abs.app.application.user.info.dto.UserInfoResponseDto;
import com.abs.app.common.constant.UserConstant;
import com.abs.app.common.exception.ResourceNotFoundException;
import com.abs.app.domain.entity.Address;
import com.abs.app.domain.entity.User;
import com.abs.app.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GetCurrentUserQueryHandler {
    private final UserRepository userRepository;

    public UserInfoResponseDto handle(String userId) {
        var user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(UserConstant.USER_NOT_EXIST));

        return UserInfoResponseDto.builder()
                .userId(user.getUserId())
                .userName(user.getUserName())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .phoneNumber(user.getPhoneNumber())
                .picture(user.getPicture())
                .isReceiveEmail(user.isReceiveEmail())
                .gender(user.isGender())
                .status(user.getStatus().name())
                .role(user.getRole().getRoleName().toString())
                .addresses(user.getAddresses().stream().map(Address::getAddress).collect(Collectors.toSet()))
                .build();
    }
}
