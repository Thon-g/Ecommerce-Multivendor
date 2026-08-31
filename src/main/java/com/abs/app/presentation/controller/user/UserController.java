package com.abs.app.presentation.controller.user;

import com.abs.app.application.user.info.command.UpdateUserImageCommand;
import com.abs.app.application.user.info.command.UpdateUserImageCommandHandler;
import com.abs.app.application.user.info.command.UpdateUserProfileCommand;
import com.abs.app.application.user.info.command.UpdateUserProfileCommandHandler;
import com.abs.app.application.user.info.dto.UpdateProfileRequestDto;
import com.abs.app.application.user.info.dto.UserInfoResponseDto;
import com.abs.app.application.user.info.query.GetCurrentUserQueryHandler;
import com.abs.app.common.constant.UserConstant;
import com.abs.app.common.response.ApiResponse;
import com.abs.app.infrastructure.security.SecurityUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated()")
public class UserController {
    private final GetCurrentUserQueryHandler getCurrentUserQueryHandler;
    private final UpdateUserProfileCommandHandler updateUserProfileCommandHandler;
    private final UpdateUserImageCommandHandler updateUserImageCommandHandler;

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserInfoResponseDto>> getCurrentUser() {
        String userId = SecurityUtils.getCurrentUserId();
        UserInfoResponseDto userInfo = getCurrentUserQueryHandler.handle(userId);
        return ResponseEntity.ok(new ApiResponse<>(true, UserConstant.GET_USER_INFO_SUCCESS, userInfo));
    }

    @PutMapping("/update-profile")
    public ResponseEntity<ApiResponse<Void>> updateProfile(@Valid @RequestBody UpdateProfileRequestDto dto) {
        String userId = SecurityUtils.getCurrentUserId();
        updateUserProfileCommandHandler.handle(new UpdateUserProfileCommand(
                userId,
                dto.getFirstName(),
                dto.getLastName(),
                dto.getPhoneNumber(),
                dto.isGender()));
        return ResponseEntity.ok(new ApiResponse<>(true, UserConstant.UPDATE_USER_INFO_SUCCESS, null));
    }

    @PutMapping("/update-image")
    public ResponseEntity<ApiResponse<Void>> updatePicture(@RequestParam("file") MultipartFile file) {
        String userId = SecurityUtils.getCurrentUserId();
        updateUserImageCommandHandler.handle(new UpdateUserImageCommand(userId, file));
        return ResponseEntity.ok(new ApiResponse<>(true, UserConstant.UPDATE_USER_IMAGE_SUCCESS, null));
    }
}
