package com.abs.app.unit.application.auth.command;

import com.abs.app.application.auth.command.RegisterUserCommand;
import com.abs.app.application.auth.command.RegisterUserCommandHandler;
import com.abs.app.application.auth.dto.AuthResponseDto;
import com.abs.app.common.constant.AuthConstant;
import com.abs.app.common.constant.RoleConstant;
import com.abs.app.common.exception.BusinessException;
import com.abs.app.common.exception.DuplicateResourceException;
import com.abs.app.common.exception.ResourceNotFoundException;
import com.abs.app.domain.entity.Cart;
import com.abs.app.domain.entity.Role;
import com.abs.app.domain.entity.User;
import com.abs.app.domain.entity.Wishlist;
import com.abs.app.domain.entity.enums.RoleUser;
import com.abs.app.domain.repository.CartRepository;
import com.abs.app.domain.repository.RoleRepository;
import com.abs.app.domain.repository.UserRepository;
import com.abs.app.domain.repository.WishlistRepository;
import com.abs.app.domain.service.OtpTokenService;
import com.abs.app.infrastructure.security.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RegisterUserCommandHandlerTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private CartRepository cartRepository;

    @Mock
    private WishlistRepository wishlistRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private OtpTokenService otpTokenService;

    @InjectMocks
    private RegisterUserCommandHandler handler;

    private RegisterUserCommand command;
    private Role role;

    @BeforeEach
    void setUp() {
        command = new RegisterUserCommand("test@gmail.com", "Password@123", "John", "Doe");
        role = new Role();
        role.setRoleName(RoleUser.CUSTOMER);
    }

    @Test
    @DisplayName("Đăng ký thất bại: Ném ngoại lệ khi Email đã tồn tại")
    void shouldThrowDuplicateResourceException_WhenEmailExists() {
        when(userRepository.existsByEmail(command.getEmail())).thenReturn(true);

        assertThatThrownBy(() -> handler.handle(command))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessage(AuthConstant.EMAIL_EXIST);

        verifyNoInteractions(otpTokenService, roleRepository, passwordEncoder);
    }

    @Test
    @DisplayName("Đăng ký thất bại: Ném ngoại lệ khi Email chưa được xác thực OTP")
    void shouldThrowBusinessException_WhenEmailNotVerified() {
        when(userRepository.existsByEmail(command.getEmail())).thenReturn(false);
        when(otpTokenService.isEmailVerified(command.getEmail())).thenReturn(false);

        assertThatThrownBy(() -> handler.handle(command))
                .isInstanceOf(BusinessException.class)
                .hasMessage(AuthConstant.EMAIL_NOT_VERIFIED);
    }

    @Test
    @DisplayName("Đăng ký thất bại: Ném ngoại lệ khi Role CUSTOMER không tìm thấy trong DB")
    void shouldThrowResourceNotFoundException_WhenRoleNotFound() {
        when(userRepository.existsByEmail(command.getEmail())).thenReturn(false);
        when(otpTokenService.isEmailVerified(command.getEmail())).thenReturn(true);
        when(roleRepository.findByRoleName(RoleUser.CUSTOMER)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> handler.handle(command))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage(RoleConstant.ROLE_NOT_EXIST);
    }

    @Test
    @DisplayName("Đăng ký thành công: Lưu User, tạo Cart, tạo Wishlist và trả về Token")
    void shouldRegisterUserSuccessfully_AndReturnAccessToken() {
        when(userRepository.existsByEmail(command.getEmail())).thenReturn(false);
        when(otpTokenService.isEmailVerified(command.getEmail())).thenReturn(true);
        when(roleRepository.findByRoleName(RoleUser.CUSTOMER)).thenReturn(Optional.of(role));
        when(passwordEncoder.encode(command.getPassword())).thenReturn("encodedPassword");
        when(jwtTokenProvider.generateAccessToken(anyString(), eq("CUSTOMER"))).thenReturn("mock-access-token");

        AuthResponseDto response = handler.handle(command);

        assertThat(response).isNotNull();
        assertThat(response.getAccessToken()).isEqualTo("mock-access-token");

        verify(userRepository).save(any(User.class));
        verify(otpTokenService).invalidateEmailVerified(command.getEmail());
        verify(cartRepository).save(any(Cart.class));
        verify(wishlistRepository).save(any(Wishlist.class));
    }
}
