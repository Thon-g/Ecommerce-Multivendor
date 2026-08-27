package com.abs.app.presentation.controller.auth;

import com.abs.app.application.auth.command.RegisterUserCommand;
import com.abs.app.application.auth.command.RegisterUserCommandHandler;
import com.abs.app.application.auth.command.ResetPasswordCommand;
import com.abs.app.application.auth.command.ResetPasswordCommandHandler;
import com.abs.app.application.auth.command.VerifyOtpCommand;
import com.abs.app.application.auth.command.VerifyOtpCommandHandler;
import com.abs.app.application.auth.dto.RegisterRequestDto;
import com.abs.app.application.auth.dto.ResetPasswordRequestDto;
import com.abs.app.application.auth.dto.VerifyOtpRequestDto;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.abs.app.application.auth.command.ChangePasswordCommand;
import com.abs.app.application.auth.command.ChangePasswordCommandHandler;
import com.abs.app.application.auth.command.ForgotPasswordCommand;
import com.abs.app.application.auth.command.ForgotPasswordCommandHandler;
import com.abs.app.application.auth.command.GenerateOtpCommand;
import com.abs.app.application.auth.command.GenerateOtpCommandHandler;
import com.abs.app.application.auth.command.LoginUserCommand;
import com.abs.app.application.auth.command.LoginUserCommandHandler;
import com.abs.app.application.auth.command.RefreshTokenCommand;
import com.abs.app.application.auth.command.RefreshTokenCommandHandler;
import com.abs.app.application.auth.dto.ChangePasswordRequestDto;
import com.abs.app.application.auth.dto.ForgotPasswordRequestDto;
import com.abs.app.application.auth.dto.GenerateOtpRequestDto;
import com.abs.app.application.auth.dto.GenerateOtpResponseDto;
import com.abs.app.application.auth.dto.LoginRequestDto;
import com.abs.app.application.auth.dto.AuthResponseDto;
import com.abs.app.common.constant.AuthConstant;
import com.abs.app.common.exception.UnauthorizedException;
import com.abs.app.common.response.ApiResponse;
import com.abs.app.infrastructure.security.SecurityUtils;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final LoginUserCommandHandler loginHandler;
    private final RegisterUserCommandHandler registerHandler;
    private final ChangePasswordCommandHandler changePasswordHandler;
    private final ForgotPasswordCommandHandler forgotPasswordCommandHandler;
    private final RefreshTokenCommandHandler refreshTokenCommandHandler;
    private final ResetPasswordCommandHandler resetPasswordCommandHandler;
    private final GenerateOtpCommandHandler generateOtpCommandHandler;
    private final VerifyOtpCommandHandler verifyOtpCommandHandler;
    private final AuthCookieHelper authCookieHelper;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<Void>> login(@Valid @RequestBody LoginRequestDto dto) {
        AuthResponseDto response = loginHandler
                .handle(new LoginUserCommand(dto.getEmail(), dto.getPassword(), dto.isRememberMe()));
        return ResponseEntity.ok()
                .headers(authCookieHelper.createAuthCookieHeaders(response))
                .body(new ApiResponse<>(true, AuthConstant.LOGIN_SUCCESS, null));
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<Void>> register(@Valid @RequestBody RegisterRequestDto dto) {
        AuthResponseDto responseDto = registerHandler.handle(
                new RegisterUserCommand(dto.getEmail(), dto.getPassword(), dto.getFirstName(), dto.getLastName()));
        return ResponseEntity.ok()
                .headers(authCookieHelper.createAuthCookieHeaders(responseDto))
                .body(new ApiResponse<>(true, AuthConstant.REGISTER_SUCCESS, null));
    }

    @PostMapping("/change-password")
    public ResponseEntity<ApiResponse<Void>> changePassword(
            @Valid @RequestBody ChangePasswordRequestDto dto) {
        String userId = SecurityUtils.getCurrentUserId();
        AuthResponseDto response = changePasswordHandler.handle(new ChangePasswordCommand(
                userId, dto.getCurrentPassword(), dto.getNewPassword(), dto.getRePassword()));
        return ResponseEntity.ok()
                .headers(authCookieHelper.createAuthCookieHeaders(response))
                .body(new ApiResponse<>(true, AuthConstant.CHANGE_PASSWORD_SUCCESS, null));
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponse<Void>> forgotPassword(@Valid @RequestBody ForgotPasswordRequestDto dto) {
        forgotPasswordCommandHandler.handle(new ForgotPasswordCommand(dto.getEmail()));
        return ResponseEntity.ok(new ApiResponse<>(true, AuthConstant.FORGOT_PASSWORD_SUCCESS, null));
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<ApiResponse<Void>> refreshToken(HttpServletRequest request) {
        String refreshToken = authCookieHelper.getRefreshToken(request)
                .orElseThrow(() -> new UnauthorizedException(AuthConstant.INVALID_TOKEN));
        if (refreshToken == null || refreshToken.isBlank()) {
            throw new UnauthorizedException(AuthConstant.INVALID_TOKEN);
        }
        AuthResponseDto response = refreshTokenCommandHandler.handle(new RefreshTokenCommand(refreshToken));
        return ResponseEntity.ok()
                .headers(authCookieHelper.createAuthCookieHeaders(response))
                .body(new ApiResponse<>(true, AuthConstant.REFRESH_TOKEN_SUCCESS, null));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse<Void>> resetPassword(@Valid @RequestBody ResetPasswordRequestDto dto) {
        resetPasswordCommandHandler.handle(new ResetPasswordCommand(dto.getToken(), dto.getNewPassword()));
        return ResponseEntity.ok(new ApiResponse<>(true, AuthConstant.RESET_PASSWORD_SUCCESS, null));
    }

    @PostMapping("/send-otp")
    public ResponseEntity<ApiResponse<GenerateOtpResponseDto>> sendOtp(
            @Valid @RequestBody GenerateOtpRequestDto dto) {
        GenerateOtpResponseDto response = generateOtpCommandHandler.handle(new GenerateOtpCommand(dto.getEmail()));
        return ResponseEntity.ok(new ApiResponse<>(true, response.getMessage(), response));
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<ApiResponse<Void>> verifyOtp(@Valid @RequestBody VerifyOtpRequestDto dto) {
        verifyOtpCommandHandler.handle(new VerifyOtpCommand(dto.getEmail(), dto.getOtp()));
        return ResponseEntity.ok(new ApiResponse<>(true, AuthConstant.VERIFY_OTP_SUCCESS, null));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout() {
        return ResponseEntity.ok()
                .headers(authCookieHelper.clearAuthCookieHeaders())
                .body(new ApiResponse<>(true, AuthConstant.LOGOUT_SUCCESS, null));
    }
}
