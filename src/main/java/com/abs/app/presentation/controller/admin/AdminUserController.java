package com.abs.app.presentation.controller.admin;

import com.abs.app.application.admin.usermanager.command.UpdateUserStatusCommand;
import com.abs.app.application.admin.usermanager.command.UpdateUserStatusCommandHandler;
import com.abs.app.application.admin.usermanager.dto.UserDetailResponseDto;
import com.abs.app.application.admin.usermanager.dto.UserResponseDto;
import com.abs.app.application.admin.usermanager.query.GetAllUsersQuery;
import com.abs.app.application.admin.usermanager.query.GetAllUsersQueryHandler;
import com.abs.app.application.admin.usermanager.query.GetUserDetailQuery;
import com.abs.app.application.admin.usermanager.query.GetUserDetailQueryHandler;
import com.abs.app.common.constant.UserConstant;
import com.abs.app.common.response.ApiResponse;
import com.abs.app.common.response.PageResponse;
import com.abs.app.domain.entity.enums.UserStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/users")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminUserController {

    private final GetAllUsersQueryHandler getAllUsersQueryHandler;
    private final GetUserDetailQueryHandler getUserDetailQueryHandler;
    private final UpdateUserStatusCommandHandler updateUserStatusCommandHandler;

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<UserResponseDto>>> getAllUsers(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) UserStatus status,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {

        GetAllUsersQuery query = new GetAllUsersQuery(keyword, status, page, size);
        PageResponse<UserResponseDto> response = getAllUsersQueryHandler.handle(query);

        return ResponseEntity.ok(new ApiResponse<>(
                true,
                UserConstant.ADMIN_GET_USERS_SUCCESS,
                response
        ));
    }

    @GetMapping("/{userId}")
    public ResponseEntity<ApiResponse<UserDetailResponseDto>> getUserDetail(@PathVariable String userId) {
        GetUserDetailQuery query = new GetUserDetailQuery(userId);
        UserDetailResponseDto response = getUserDetailQueryHandler.handle(query);

        return ResponseEntity.ok(new ApiResponse<>(
                true,
                UserConstant.ADMIN_GET_USER_DETAIL_SUCCESS,
                response
        ));
    }

    @PatchMapping("/{userId}/status")
    public ResponseEntity<ApiResponse<UserResponseDto>> updateUserStatus(
            @PathVariable String userId,
            @RequestParam UserStatus status) {

        UpdateUserStatusCommand command = new UpdateUserStatusCommand(userId, status);
        UserResponseDto response = updateUserStatusCommandHandler.handle(command);

        return ResponseEntity.ok(new ApiResponse<>(
                true,
                UserConstant.ADMIN_UPDATE_USER_STATUS_SUCCESS,
                response
        ));
    }
}
