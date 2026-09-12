package com.abs.app.unit.application.seller.profile.command;

import com.abs.app.application.seller.profile.command.UpdateSellerAddressCommand;
import com.abs.app.application.seller.profile.command.UpdateSellerAddressCommandHandler;
import com.abs.app.application.seller.profile.dto.SellerResponseDto;
import com.abs.app.common.constant.SellerConstant;
import com.abs.app.common.exception.ResourceNotFoundException;
import com.abs.app.domain.entity.Address;
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
class UpdateSellerAddressCommandHandlerTest {

    @Mock
    private SellerRepository sellerRepository;

    @InjectMocks
    private UpdateSellerAddressCommandHandler handler;

    private UpdateSellerAddressCommand command;
    private Seller mockSeller;
    private MockedStatic<SecurityUtils> mockedSecurityUtils;

    @BeforeEach
    void setUp() {
        command = new UpdateSellerAddressCommand("Shop Name", "Shop Locality", "123 Shop St", "Shop City", "Shop State", "100000", "0123456789");

        mockSeller = new Seller();
        mockSeller.setSellerId("SELLER_ID");

        mockedSecurityUtils = mockStatic(SecurityUtils.class);
    }

    @AfterEach
    void tearDown() {
        mockedSecurityUtils.close();
    }

    @Test
    @DisplayName("Cập nhật địa chỉ Seller thất bại: Ném ngoại lệ khi user chưa đăng nhập")
    void shouldThrowResourceNotFoundException_WhenUserIdNull() {
        mockedSecurityUtils.when(SecurityUtils::getCurrentUserId).thenReturn(null);

        assertThatThrownBy(() -> handler.handle(command))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage(SellerConstant.SELLER_NOT_FOUND);

        verifyNoInteractions(sellerRepository);
    }

    @Test
    @DisplayName("Cập nhật địa chỉ Seller thất bại: Ném ngoại lệ khi Seller không tồn tại")
    void shouldThrowResourceNotFoundException_WhenSellerNotFound() {
        mockedSecurityUtils.when(SecurityUtils::getCurrentUserId).thenReturn("user123");
        when(sellerRepository.findByUserId("user123")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> handler.handle(command))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage(SellerConstant.SELLER_NOT_FOUND);
    }

    @Test
    @DisplayName("Cập nhật địa chỉ Seller thành công (Tạo mới Address)")
    void shouldUpdateSellerAddressSuccessfully_WhenAddressIsNull() {
        mockedSecurityUtils.when(SecurityUtils::getCurrentUserId).thenReturn("user123");
        when(sellerRepository.findByUserId("user123")).thenReturn(Optional.of(mockSeller));
        when(sellerRepository.save(any(Seller.class))).thenAnswer(i -> i.getArgument(0));

        SellerResponseDto response = handler.handle(command);

        assertThat(response).isNotNull();
        assertThat(mockSeller.getPickupAddress()).isNotNull();
        assertThat(mockSeller.getPickupAddress().getName()).isEqualTo("Shop Name");
        assertThat(mockSeller.getPickupAddress().getAddress()).isEqualTo("123 Shop St");

        verify(sellerRepository).save(mockSeller);
    }

    @Test
    @DisplayName("Cập nhật địa chỉ Seller thành công (Cập nhật Address đã có)")
    void shouldUpdateSellerAddressSuccessfully_WhenAddressExists() {
        Address oldAddress = new Address();
        oldAddress.setName("Old Shop Name");
        mockSeller.setPickupAddress(oldAddress);

        mockedSecurityUtils.when(SecurityUtils::getCurrentUserId).thenReturn("user123");
        when(sellerRepository.findByUserId("user123")).thenReturn(Optional.of(mockSeller));
        when(sellerRepository.save(any(Seller.class))).thenAnswer(i -> i.getArgument(0));

        SellerResponseDto response = handler.handle(command);

        assertThat(response).isNotNull();
        assertThat(mockSeller.getPickupAddress().getName()).isEqualTo("Shop Name");

        verify(sellerRepository).save(mockSeller);
    }
}
