package com.abs.app.unit.application.seller.profile.command;

import com.abs.app.application.seller.profile.command.UpdateSellerBankCommand;
import com.abs.app.application.seller.profile.command.UpdateSellerBankCommandHandler;
import com.abs.app.application.seller.profile.dto.SellerResponseDto;
import com.abs.app.common.constant.SellerConstant;
import com.abs.app.common.exception.ResourceNotFoundException;
import com.abs.app.domain.entity.BankDetails;
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
class UpdateSellerBankCommandHandlerTest {

    @Mock
    private SellerRepository sellerRepository;

    @InjectMocks
    private UpdateSellerBankCommandHandler handler;

    private UpdateSellerBankCommand command;
    private Seller mockSeller;
    private MockedStatic<SecurityUtils> mockedSecurityUtils;

    @BeforeEach
    void setUp() {
        command = new UpdateSellerBankCommand("Account Name", "Holder Name", "IFSC123");

        mockSeller = new Seller();
        mockSeller.setSellerId("SELLER_ID");

        mockedSecurityUtils = mockStatic(SecurityUtils.class);
    }

    @AfterEach
    void tearDown() {
        mockedSecurityUtils.close();
    }

    @Test
    @DisplayName("Cập nhật ngân hàng Seller thất bại: Ném ngoại lệ khi user chưa đăng nhập")
    void shouldThrowResourceNotFoundException_WhenUserIdNull() {
        mockedSecurityUtils.when(SecurityUtils::getCurrentUserId).thenReturn(null);

        assertThatThrownBy(() -> handler.handle(command))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage(SellerConstant.SELLER_NOT_FOUND);

        verifyNoInteractions(sellerRepository);
    }

    @Test
    @DisplayName("Cập nhật ngân hàng Seller thất bại: Ném ngoại lệ khi Seller không tồn tại")
    void shouldThrowResourceNotFoundException_WhenSellerNotFound() {
        mockedSecurityUtils.when(SecurityUtils::getCurrentUserId).thenReturn("user123");
        when(sellerRepository.findByUserId("user123")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> handler.handle(command))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage(SellerConstant.SELLER_NOT_FOUND);
    }

    @Test
    @DisplayName("Cập nhật ngân hàng Seller thành công (Tạo mới BankDetails)")
    void shouldUpdateSellerBankSuccessfully_WhenBankDetailsIsNull() {
        mockedSecurityUtils.when(SecurityUtils::getCurrentUserId).thenReturn("user123");
        when(sellerRepository.findByUserId("user123")).thenReturn(Optional.of(mockSeller));
        when(sellerRepository.save(any(Seller.class))).thenAnswer(i -> i.getArgument(0));

        SellerResponseDto response = handler.handle(command);

        assertThat(response).isNotNull();
        assertThat(mockSeller.getBankDetails()).isNotNull();
        assertThat(mockSeller.getBankDetails().getAccountName()).isEqualTo("Account Name");
        assertThat(mockSeller.getBankDetails().getIfscCode()).isEqualTo("IFSC123");

        verify(sellerRepository).save(mockSeller);
    }

    @Test
    @DisplayName("Cập nhật ngân hàng Seller thành công (Cập nhật BankDetails đã có)")
    void shouldUpdateSellerBankSuccessfully_WhenBankDetailsExists() {
        BankDetails oldBankDetails = new BankDetails();
        oldBankDetails.setAccountName("Old Account Name");
        mockSeller.setBankDetails(oldBankDetails);

        mockedSecurityUtils.when(SecurityUtils::getCurrentUserId).thenReturn("user123");
        when(sellerRepository.findByUserId("user123")).thenReturn(Optional.of(mockSeller));
        when(sellerRepository.save(any(Seller.class))).thenAnswer(i -> i.getArgument(0));

        SellerResponseDto response = handler.handle(command);

        assertThat(response).isNotNull();
        assertThat(mockSeller.getBankDetails().getAccountName()).isEqualTo("Account Name");

        verify(sellerRepository).save(mockSeller);
    }
}
