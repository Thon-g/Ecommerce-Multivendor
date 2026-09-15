package com.abs.app.unit.application.seller.profile.command;

import com.abs.app.application.seller.profile.command.UpdateSellerProfileCommand;
import com.abs.app.application.seller.profile.command.UpdateSellerProfileCommandHandler;
import com.abs.app.application.seller.profile.dto.SellerResponseDto;
import com.abs.app.common.constant.SellerConstant;
import com.abs.app.common.exception.ResourceNotFoundException;
import com.abs.app.domain.entity.BusinessDetails;
import com.abs.app.domain.entity.Seller;
import com.abs.app.domain.repository.SellerRepository;
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
class UpdateSellerProfileCommandHandlerTest {

    @Mock
    private SellerRepository sellerRepository;

    @InjectMocks
    private UpdateSellerProfileCommandHandler handler;

    private UpdateSellerProfileCommand command;
    private Seller mockSeller;
    private MockedStatic<SecurityUtils> mockedSecurityUtils;

    @BeforeEach
    void setUp() {
        command = new UpdateSellerProfileCommand("My Business", "email@test.com", "0123456789", "123 Business St");

        mockSeller = new Seller();
        mockSeller.setSellerId("SELLER_ID");

        mockedSecurityUtils = mockStatic(SecurityUtils.class);
    }

    @AfterEach
    void tearDown() {
        mockedSecurityUtils.close();
    }

    @Test
    @DisplayName("Cập nhật Seller Profile thất bại: Ném ngoại lệ khi user chưa đăng nhập")
    void shouldThrowResourceNotFoundException_WhenUserIdNull() {
        mockedSecurityUtils.when(SecurityUtils::getCurrentUserId).thenReturn(null);

        assertThatThrownBy(() -> handler.handle(command))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage(SellerConstant.SELLER_NOT_FOUND);

        verifyNoInteractions(sellerRepository);
    }

    @Test
    @DisplayName("Cập nhật Seller Profile thất bại: Ném ngoại lệ khi không tìm thấy Seller")
    void shouldThrowResourceNotFoundException_WhenSellerNotFound() {
        mockedSecurityUtils.when(SecurityUtils::getCurrentUserId).thenReturn("user123");
        when(sellerRepository.findByUserId("user123")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> handler.handle(command))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage(SellerConstant.SELLER_NOT_FOUND);
    }

    @Test
    @DisplayName("Cập nhật Seller Profile thành công (Tạo mới BusinessDetails)")
    void shouldUpdateSellerProfileSuccessfully_WhenBusinessDetailsIsNull() {
        mockedSecurityUtils.when(SecurityUtils::getCurrentUserId).thenReturn("user123");
        when(sellerRepository.findByUserId("user123")).thenReturn(Optional.of(mockSeller));
        when(sellerRepository.save(any(Seller.class))).thenAnswer(invocation -> invocation.getArgument(0));

        SellerResponseDto response = handler.handle(command);

        assertThat(response).isNotNull();
        assertThat(mockSeller.getShopName()).isEqualTo("My Business");
        assertThat(mockSeller.getBusinessDetails()).isNotNull();
        assertThat(mockSeller.getBusinessDetails().getBusinessName()).isEqualTo("My Business");
        assertThat(mockSeller.getBusinessDetails().getBusinessEmail()).isEqualTo("email@test.com");

        verify(sellerRepository).save(mockSeller);
    }

    @Test
    @DisplayName("Cập nhật Seller Profile thành công (Cập nhật BusinessDetails đã có)")
    void shouldUpdateSellerProfileSuccessfully_WhenBusinessDetailsExists() {
        BusinessDetails details = new BusinessDetails();
        details.setBusinessName("Old Name");
        mockSeller.setBusinessDetails(details);

        mockedSecurityUtils.when(SecurityUtils::getCurrentUserId).thenReturn("user123");
        when(sellerRepository.findByUserId("user123")).thenReturn(Optional.of(mockSeller));
        when(sellerRepository.save(any(Seller.class))).thenAnswer(invocation -> invocation.getArgument(0));

        SellerResponseDto response = handler.handle(command);

        assertThat(response).isNotNull();
        assertThat(mockSeller.getShopName()).isEqualTo("My Business");
        assertThat(mockSeller.getBusinessDetails().getBusinessName()).isEqualTo("My Business");

        verify(sellerRepository).save(mockSeller);
    }
}
