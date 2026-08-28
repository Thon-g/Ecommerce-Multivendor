package com.abs.app.application.user.info.command;

import com.abs.app.common.constant.UserConstant;
import com.abs.app.common.exception.ResourceNotFoundException;
import com.abs.app.domain.entity.User;
import com.abs.app.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UpdateUserProfileCommandHandler {
    private final UserRepository userRepository;

    public void handle(UpdateUserProfileCommand command) {
        User user = userRepository.findById(command.getUserId())
                .orElseThrow(() -> new RuntimeException(UserConstant.USER_NOT_EXIST));

        user.setFirstName(command.getFirstName());
        user.setLastName(command.getLastName());
        user.setPhoneNumber(command.getPhoneNumber());
        user.setGender(command.isGender());
        userRepository.save(user);
    }

}
