package com.abs.app.unit.application.admin.sellermanager.query;

import com.abs.app.application.admin.sellermanager.query.GetAllSellersQuery;
import com.abs.app.application.admin.sellermanager.query.GetAllSellersQueryHandler;
import com.abs.app.application.seller.profile.dto.SellerResponseDto;
import com.abs.app.common.response.PageResponse;
import com.abs.app.domain.entity.Seller;
import com.abs.app.domain.entity.enums.SellerStatus;
import com.abs.app.domain.repository.SellerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetAllSellersQueryHandlerTest {

    @Mock
    private SellerRepository sellerRepository;

    @InjectMocks
    private GetAllSellersQueryHandler handler;

    private GetAllSellersQuery query;
    private Seller mockSeller;

    @BeforeEach
    void setUp() {
        query = new GetAllSellersQuery("keyword", SellerStatus.ACTIVE, 1, 10);

        mockSeller = new Seller();
        mockSeller.setSellerId("SELLER_ID");
        mockSeller.setStatus(SellerStatus.ACTIVE);
    }

    @Test
    @DisplayName("Lấy danh sách Seller thành công")
    void shouldReturnSellersSuccessfully() {
        Page<Seller> page = new PageImpl<>(List.of(mockSeller));

        when(sellerRepository.search(eq("keyword"), eq(SellerStatus.ACTIVE), any(Pageable.class))).thenReturn(page);

        PageResponse<SellerResponseDto> response = handler.handle(query);

        assertThat(response).isNotNull();
        assertThat(response.getItems()).hasSize(1);
        assertThat(response.getTotal()).isEqualTo(1);
        assertThat(response.getPage()).isEqualTo(1);
        assertThat(response.getLimit()).isEqualTo(10);

        verify(sellerRepository).search(eq("keyword"), eq(SellerStatus.ACTIVE), any(Pageable.class));
    }

    @Test
    @DisplayName("Lấy danh sách Seller rỗng thành công")
    void shouldReturnEmptySellersSuccessfully() {
        Page<Seller> page = new PageImpl<>(Collections.emptyList());

        when(sellerRepository.search(eq("keyword"), eq(SellerStatus.ACTIVE), any(Pageable.class))).thenReturn(page);

        PageResponse<SellerResponseDto> response = handler.handle(query);

        assertThat(response).isNotNull();
        assertThat(response.getItems()).isEmpty();
        assertThat(response.getTotal()).isEqualTo(0);

        verify(sellerRepository).search(eq("keyword"), eq(SellerStatus.ACTIVE), any(Pageable.class));
    }
}
