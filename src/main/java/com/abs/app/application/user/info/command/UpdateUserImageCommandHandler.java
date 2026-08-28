package com.abs.app.application.user.info.command;

import com.abs.app.common.constant.UserConstant;
import com.abs.app.domain.entity.User;
import com.abs.app.domain.repository.UserRepository;
import com.abs.app.infrastructure.file.FileStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UpdateUserImageCommandHandler {
    private final UserRepository userRepository;
    private final FileStorageService fileStorageService;

    public void handle(UpdateUserImageCommand command) {
        User user = userRepository.findById(command.getUserId())
                .orElseThrow(() -> new RuntimeException(UserConstant.USER_NOT_EXIST));

        if ((command.getPicture() != null && !command.getPicture().isEmpty())) {
            String pictureUrl = fileStorageService.storeAvatar(command.getPicture(), command.getUserId());
            user.setPicture(pictureUrl);
        }

        userRepository.save(user);
    }
}
