package com.abs.app.unit.application.seller.profile.command;

import com.abs.app.application.seller.profile.command.RegisterSellerCommand;
import com.abs.app.application.seller.profile.command.RegisterSellerCommandHandler;
import com.abs.app.application.seller.profile.dto.SellerResponseDto;
import com.abs.app.common.constant.SellerConstant;
import com.abs.app.common.constant.UserConstant;
import com.abs.app.common.exception.BusinessException;
import com.abs.app.common.exception.ResourceNotFoundException;
import com.abs.app.domain.entity.Seller;
import com.abs.app.domain.entity.User;
import com.abs.app.domain.entity.enums.SellerStatus;
import com.abs.app.domain.repository.SellerRepository;
import com.abs.app.domain.repository.UserRepository;
import com.abs.app.infrastructure.security.SecurityUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RegisterSellerCommandHandlerTest {

    @Mock
    private SellerRepository sellerRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private RegisterSellerCommandHandler handler;

    private RegisterSellerCommand command;
    private User mockUser;
    private MockedStatic<SecurityUtils> mockedSecurityUtils;

    @BeforeEach
    void setUp() {
        command = new RegisterSellerCommand();
        command.setBusinessName("New Shop");
        command.setBusinessEmail("test@shop.com");

        mockUser = new User();
        mockUser.setUserId("user123");

        mockedSecurityUtils = mockStatic(SecurityUtils.class);
    }

    @AfterEach
    void tearDown() {
        mockedSecurityUtils.close();
    }

    @Test
    @DisplayName("Đăng ký Seller thất bại: Ném ngoại lệ khi User không tồn tại")
    void shouldThrowResourceNotFoundException_WhenUserNotFound() {
        mockedSecurityUtils.when(SecurityUtils::getCurrentUserId).thenReturn("user123");
        when(userRepository.findById("user123")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> handler.handle(command))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage(UserConstant.USER_NOT_EXIST);
    }

    @Test
    @DisplayName("Đăng ký Seller thất bại: Ném ngoại lệ khi Seller đã ở trạng thái ACTIVE")
    void shouldThrowBusinessException_WhenSellerIsActive() {
        Seller activeSeller = new Seller();
        activeSeller.setStatus(SellerStatus.ACTIVE);

        mockedSecurityUtils.when(SecurityUtils::getCurrentUserId).thenReturn("user123");
        when(userRepository.findById("user123")).thenReturn(Optional.of(mockUser));
        when(sellerRepository.findByUserId("user123")).thenReturn(Optional.of(activeSeller));

        assertThatThrownBy(() -> handler.handle(command))
                .isInstanceOf(BusinessException.class)
                .hasMessage(SellerConstant.SELLER_ALREADY_EXISTS);
    }

    @Test
    @DisplayName("Đăng ký Seller thất bại: Ném ngoại lệ khi Seller đang PENDING_VERIFICATION")
    void shouldThrowBusinessException_WhenSellerIsPending() {
        Seller pendingSeller = new Seller();
        pendingSeller.setStatus(SellerStatus.PENDING_VERIFICATION);

        mockedSecurityUtils.when(SecurityUtils::getCurrentUserId).thenReturn("user123");
        when(userRepository.findById("user123")).thenReturn(Optional.of(mockUser));
        when(sellerRepository.findByUserId("user123")).thenReturn(Optional.of(pendingSeller));

        assertThatThrownBy(() -> handler.handle(command))
                .isInstanceOf(BusinessException.class)
                .hasMessage(SellerConstant.SELLER_PENDING_EXISTS);
    }

    @Test
    @DisplayName("Đăng ký Seller thất bại: Ném ngoại lệ khi Seller bị BANNED")
    void shouldThrowBusinessException_WhenSellerIsBanned() {
        Seller bannedSeller = new Seller();
        bannedSeller.setStatus(SellerStatus.BANNED);

        mockedSecurityUtils.when(SecurityUtils::getCurrentUserId).thenReturn("user123");
        when(userRepository.findById("user123")).thenReturn(Optional.of(mockUser));
        when(sellerRepository.findByUserId("user123")).thenReturn(Optional.of(bannedSeller));

        assertThatThrownBy(() -> handler.handle(command))
                .isInstanceOf(BusinessException.class)
                .hasMessage(SellerConstant.SELLER_BANNED);
    }

    @Test
    @DisplayName("Đăng ký lại Seller thành công khi Seller đang SUSPENDED/DEACTIVATED/CLOSED")
    void shouldReRegisterSellerSuccessfully_WhenSellerIsClosed() {
        Seller closedSeller = new Seller();
        closedSeller.setStatus(SellerStatus.CLOSED);

        mockedSecurityUtils.when(SecurityUtils::getCurrentUserId).thenReturn("user123");
        when(userRepository.findById("user123")).thenReturn(Optional.of(mockUser));
        when(sellerRepository.findByUserId("user123")).thenReturn(Optional.of(closedSeller));
        when(sellerRepository.save(any(Seller.class))).thenAnswer(i -> i.getArgument(0));

        SellerResponseDto response = handler.handle(command);

        assertThat(response).isNotNull();
        assertThat(closedSeller.getStatus()).isEqualTo(SellerStatus.PENDING_VERIFICATION);
        assertThat(closedSeller.getShopName()).isEqualTo("New Shop");

        verify(sellerRepository).save(closedSeller);
    }

    @Test
    @DisplayName("Đăng ký Seller mới thành công")
    void shouldRegisterNewSellerSuccessfully() {
        mockedSecurityUtils.when(SecurityUtils::getCurrentUserId).thenReturn("user123");
        when(userRepository.findById("user123")).thenReturn(Optional.of(mockUser));
        when(sellerRepository.findByUserId("user123")).thenReturn(Optional.empty());
        when(sellerRepository.save(any(Seller.class))).thenAnswer(i -> {
            Seller s = i.getArgument(0);
            return s;
        });

        SellerResponseDto response = handler.handle(command);

        assertThat(response).isNotNull();
        assertThat(response.getBusinessName()).isEqualTo("New Shop");

        verify(sellerRepository).save(any(Seller.class));
    }
}
