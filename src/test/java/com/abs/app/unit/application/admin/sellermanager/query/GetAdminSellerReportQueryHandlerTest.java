package com.abs.app.unit.application.admin.sellermanager.query;

import com.abs.app.application.admin.sellermanager.query.GetAdminSellerReportQuery;
import com.abs.app.application.admin.sellermanager.query.GetAdminSellerReportQueryHandler;
import com.abs.app.application.seller.report.dto.SellerReportResponseDto;
import com.abs.app.common.constant.SellerConstant;
import com.abs.app.common.exception.ResourceNotFoundException;
import com.abs.app.domain.entity.Seller;
import com.abs.app.domain.entity.SellerReport;
import com.abs.app.domain.repository.SellerReportRepository;
import com.abs.app.domain.repository.SellerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetAdminSellerReportQueryHandlerTest {

    @Mock
    private SellerRepository sellerRepository;

    @Mock
    private SellerReportRepository sellerReportRepository;

    @InjectMocks
    private GetAdminSellerReportQueryHandler handler;

    private Seller mockSeller;
    private SellerReport mockReport;

    @BeforeEach
    void setUp() {
        mockSeller = new Seller();
        mockSeller.setSellerId("SELLER_01");

        mockReport = new SellerReport();
        mockReport.setId(10L);
        mockReport.setSeller(mockSeller);
        mockReport.setTotalEarnings(10000000L);
        mockReport.setTotalSales(12000000L);
        mockReport.setTotalRefunds(500000L);
        mockReport.setTotalTax(1000000L);
        mockReport.setNetEarnings(8500000L);
        mockReport.setTotalOrder(50);
        mockReport.setCanceledOrders(2);
        mockReport.setTotalTransactions(48);
    }

    @Test
    @DisplayName("Lấy báo cáo hoạt động Seller thành công khi có dữ liệu báo cáo")
    void shouldReturnSellerReportSuccessfully() {
        when(sellerRepository.findBySellerId("SELLER_01")).thenReturn(Optional.of(mockSeller));
        when(sellerReportRepository.findBySellerId("SELLER_01")).thenReturn(Optional.of(mockReport));

        SellerReportResponseDto response = handler.handle(new GetAdminSellerReportQuery("SELLER_01"));

        assertThat(response).isNotNull();
        assertThat(response.getTotalEarnings()).isEqualTo(10000000L);
        assertThat(response.getTotalSales()).isEqualTo(12000000L);
        assertThat(response.getTotalRefunds()).isEqualTo(500000L);
        assertThat(response.getTotalTax()).isEqualTo(1000000L);
        assertThat(response.getNetEarnings()).isEqualTo(8500000L);
        assertThat(response.getTotalOrder()).isEqualTo(50);
        assertThat(response.getCanceledOrders()).isEqualTo(2);
        assertThat(response.getTotalTransactions()).isEqualTo(48);

        verify(sellerRepository).findBySellerId("SELLER_01");
        verify(sellerReportRepository).findBySellerId("SELLER_01");
    }

    @Test
    @DisplayName("Lấy báo cáo hoạt động Seller thành công khi chưa có dữ liệu báo cáo (trả về giá trị 0)")
    void shouldReturnDefaultReport_WhenReportIsEmpty() {
        when(sellerRepository.findBySellerId("SELLER_01")).thenReturn(Optional.of(mockSeller));
        when(sellerReportRepository.findBySellerId("SELLER_01")).thenReturn(Optional.empty());

        SellerReportResponseDto response = handler.handle(new GetAdminSellerReportQuery("SELLER_01"));

        assertThat(response).isNotNull();
        assertThat(response.getTotalEarnings()).isEqualTo(0L);
        assertThat(response.getTotalSales()).isEqualTo(0L);
        assertThat(response.getTotalOrder()).isEqualTo(0);
    }

    @Test
    @DisplayName("Lấy báo cáo hoạt động Seller thất bại: Ném ResourceNotFoundException khi Seller không tồn tại")
    void shouldThrowException_WhenSellerNotFound() {
        when(sellerRepository.findBySellerId("UNKNOWN")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> handler.handle(new GetAdminSellerReportQuery("UNKNOWN")))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage(SellerConstant.SELLER_NOT_FOUND);
    }
}

