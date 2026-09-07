package com.abs.app.unit.application.seller.profile.query;

import com.abs.app.application.seller.profile.dto.SellerResponseDto;
import com.abs.app.application.seller.profile.query.GetCurrentSellerQuery;
import com.abs.app.application.seller.profile.query.GetCurrentSellerQueryHandler;
import com.abs.app.common.constant.SellerConstant;
import com.abs.app.common.exception.ResourceNotFoundException;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetCurrentSellerQueryHandlerTest {

    @Mock
    private SellerRepository sellerRepository;

    @InjectMocks
    private GetCurrentSellerQueryHandler handler;

    private GetCurrentSellerQuery query;
    private Seller mockSeller;
    private MockedStatic<SecurityUtils> mockedSecurityUtils;

    @BeforeEach
    void setUp() {
        query = new GetCurrentSellerQuery();

        mockSeller = new Seller();
        mockSeller.setSellerId("SELLER_ID");
        mockSeller.setShopName("My Shop");

        com.abs.app.domain.entity.BusinessDetails businessDetails = new com.abs.app.domain.entity.BusinessDetails();
        businessDetails.setBusinessName("My Shop");
        mockSeller.setBusinessDetails(businessDetails);

        mockedSecurityUtils = mockStatic(SecurityUtils.class);
    }

    @AfterEach
    void tearDown() {
        mockedSecurityUtils.close();
    }

    @Test
    @DisplayName("Lấy thông tin Seller hiện tại thất bại: Ném ngoại lệ khi user chưa đăng nhập (userId null)")
    void shouldThrowResourceNotFoundException_WhenUserIdNull() {
        mockedSecurityUtils.when(SecurityUtils::getCurrentUserId).thenReturn(null);

        assertThatThrownBy(() -> handler.handle(query))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage(SellerConstant.SELLER_NOT_FOUND);

        verifyNoInteractions(sellerRepository);
    }

    @Test
    @DisplayName("Lấy thông tin Seller hiện tại thất bại: Ném ngoại lệ khi không tìm thấy Seller trong DB")
    void shouldThrowResourceNotFoundException_WhenSellerNotFound() {
        mockedSecurityUtils.when(SecurityUtils::getCurrentUserId).thenReturn("user123");
        when(sellerRepository.findByUserId("user123")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> handler.handle(query))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage(SellerConstant.SELLER_NOT_FOUND);
    }

    @Test
    @DisplayName("Lấy thông tin Seller hiện tại thành công")
    void shouldReturnSellerResponseDtoSuccessfully() {
        mockedSecurityUtils.when(SecurityUtils::getCurrentUserId).thenReturn("user123");
        when(sellerRepository.findByUserId("user123")).thenReturn(Optional.of(mockSeller));

        SellerResponseDto response = handler.handle(query);

        assertThat(response).isNotNull();
        assertThat(response.getSellerId()).isEqualTo("SELLER_ID");
        assertThat(response.getBusinessName()).isEqualTo("My Shop");

        verify(sellerRepository).findByUserId("user123");
    }
}
