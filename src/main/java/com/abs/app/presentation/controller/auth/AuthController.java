package com.abs.app.presentation.controller.auth;

import com.abs.app.application.auth.command.RegisterUserCommand;
import com.abs.app.application.auth.command.RegisterUserCommandHandler;
import com.abs.app.application.auth.dto.AuthResponseDto;
import com.abs.app.application.auth.dto.RegisterRequestDto;
import com.abs.app.common.constant.AuthConstant;
import com.abs.app.common.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final RegisterUserCommandHandler registerUserCommandHandler;
    private final AuthCookieHelper authCookieHelper;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<Void>> register(@Valid @RequestBody RegisterRequestDto dto) {
        AuthResponseDto responseDto = registerUserCommandHandler.handle(
                new RegisterUserCommand(dto.getEmail(), dto.getPassword(), dto.getFirstName(), dto.getLastName()));
        return ResponseEntity.ok().headers(authCookieHelper.createAuthCookieHeaders(responseDto))
                .body(new ApiResponse<>(true, AuthConstant.REGISTER_SUCCESS, null));
    }
}
