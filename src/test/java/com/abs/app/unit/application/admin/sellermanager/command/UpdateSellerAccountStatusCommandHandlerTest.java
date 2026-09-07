package com.abs.app.unit.application.admin.sellermanager.command;

import com.abs.app.application.admin.sellermanager.command.UpdateSellerAccountStatusCommand;
import com.abs.app.application.admin.sellermanager.command.UpdateSellerAccountStatusCommandHandler;
import com.abs.app.application.seller.profile.dto.SellerResponseDto;
import com.abs.app.common.exception.ResourceNotFoundException;
import com.abs.app.domain.entity.Seller;
import com.abs.app.domain.entity.enums.SellerStatus;
import com.abs.app.domain.service.SellerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UpdateSellerAccountStatusCommandHandlerTest {

    @Mock
    private SellerService sellerService;

    @InjectMocks
    private UpdateSellerAccountStatusCommandHandler handler;

    private UpdateSellerAccountStatusCommand command;
    private Seller mockSeller;

    @BeforeEach
    void setUp() {
        command = new UpdateSellerAccountStatusCommand("SELLER_ID", SellerStatus.ACTIVE);

        mockSeller = new Seller();
        mockSeller.setSellerId("SELLER_ID");
        mockSeller.setStatus(SellerStatus.ACTIVE);
    }

    @Test
    @DisplayName("Cập nhật trạng thái Seller thất bại: Ném ngoại lệ khi Seller không tồn tại")
    void shouldThrowException_WhenSellerNotFound() {
        when(sellerService.updateSellerStatus("SELLER_ID", SellerStatus.ACTIVE))
                .thenThrow(new ResourceNotFoundException("Seller không tồn tại"));

        assertThatThrownBy(() -> handler.handle(command))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Seller không tồn tại");
    }

    @Test
    @DisplayName("Cập nhật trạng thái Seller thành công")
    void shouldUpdateSellerStatusSuccessfully() {
        when(sellerService.updateSellerStatus("SELLER_ID", SellerStatus.ACTIVE)).thenReturn(mockSeller);

        SellerResponseDto response = handler.handle(command);

        assertThat(response).isNotNull();
        assertThat(response.getSellerId()).isEqualTo("SELLER_ID");

        verify(sellerService).updateSellerStatus("SELLER_ID", SellerStatus.ACTIVE);
    }
}
