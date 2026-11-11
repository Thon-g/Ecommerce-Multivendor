package com.abs.app.unit.application.admin.sellermanager.query;

import com.abs.app.application.admin.sellermanager.dto.AdminSellerDetailResponseDto;
import com.abs.app.application.admin.sellermanager.query.GetAdminSellerDetailQuery;
import com.abs.app.application.admin.sellermanager.query.GetAdminSellerDetailQueryHandler;
import com.abs.app.common.constant.SellerConstant;
import com.abs.app.common.exception.ResourceNotFoundException;
import com.abs.app.domain.entity.*;
import com.abs.app.domain.entity.enums.SellerStatus;
import com.abs.app.domain.entity.enums.UserStatus;
import com.abs.app.domain.repository.ProductRepository;
import com.abs.app.domain.repository.SellerReportRepository;
import com.abs.app.domain.repository.SellerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetAdminSellerDetailQueryHandlerTest {

    @Mock
    private SellerRepository sellerRepository;

    @Mock
    private SellerReportRepository sellerReportRepository;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private GetAdminSellerDetailQueryHandler handler;

    private Seller mockSeller;
    private SellerReport mockReport;

    @BeforeEach
    void setUp() {
        User user = new User();
        user.setUserId("USER_01");
        user.setUserName("shopkeeper");
        user.setFirstName("Van A");
        user.setLastName("Nguyen");
        user.setEmail("shop@example.com");
        user.setPhoneNumber("0912345678");
        user.setStatus(UserStatus.ACTIVE);
        user.setCreatedAt(LocalDateTime.now());

        BusinessDetails businessDetails = new BusinessDetails();
        businessDetails.setBusinessName("Tech Shop Official");
        businessDetails.setBusinessEmail("contact@techshop.vn");
        businessDetails.setBusinessPhone("0987654321");
        businessDetails.setBusinessAddress("123 Cau Giay, Ha Noi");
        businessDetails.setLogo("logo.png");
        businessDetails.setBanner("banner.png");

        BankDetails bankDetails = new BankDetails();
        bankDetails.setAccountName("Tech Shop");
        bankDetails.setAccountHolderName("Nguyen Van A");
        bankDetails.setIfscCode("VCB12345");

        Address pickupAddress = new Address();
        pickupAddress.setId(1L);
        pickupAddress.setName("Kho Hang Ha Noi");
        pickupAddress.setLocality("Dich Vong");
        pickupAddress.setAddress("So 10 Ngo 20 Dich Vong Hau");
        pickupAddress.setCity("Ha Noi");
        pickupAddress.setState("Ha Noi");
        pickupAddress.setPinCode("100000");
        pickupAddress.setPhone("0987654321");

        mockSeller = new Seller();
        mockSeller.setSellerId("SELLER_01");
        mockSeller.setShopName("TechShop");
        mockSeller.setStatus(SellerStatus.ACTIVE);
        mockSeller.setGstin("GSTIN123456");
        mockSeller.setUser(user);
        mockSeller.setBusinessDetails(businessDetails);
        mockSeller.setBankDetails(bankDetails);
        mockSeller.setPickupAddress(pickupAddress);

        mockReport = new SellerReport();
        mockReport.setId(10L);
        mockReport.setSeller(mockSeller);
        mockReport.setTotalEarnings(50000000L);
        mockReport.setTotalSales(60000000L);
        mockReport.setTotalRefunds(1000000L);
        mockReport.setTotalTax(5000000L);
        mockReport.setNetEarnings(44000000L);
        mockReport.setTotalOrder(120);
        mockReport.setCanceledOrders(5);
        mockReport.setTotalTransactions(115);
    }

    @Test
    @DisplayName("Lấy chi tiết Seller KYC thành công khi đầy đủ thông tin")
    void shouldReturnSellerDetailSuccessfully() {
        when(sellerRepository.findBySellerId("SELLER_01")).thenReturn(Optional.of(mockSeller));
        when(sellerReportRepository.findBySellerId("SELLER_01")).thenReturn(Optional.of(mockReport));
        when(productRepository.countBySellerId("SELLER_01")).thenReturn(25L);

        AdminSellerDetailResponseDto response = handler.handle(new GetAdminSellerDetailQuery("SELLER_01"));

        assertThat(response).isNotNull();
        assertThat(response.getSellerId()).isEqualTo("SELLER_01");
        assertThat(response.getShopName()).isEqualTo("TechShop");
        assertThat(response.getStatus()).isEqualTo(SellerStatus.ACTIVE);
        assertThat(response.getGstin()).isEqualTo("GSTIN123456");
        assertThat(response.getTotalProducts()).isEqualTo(25L);

        assertThat(response.getOwner()).isNotNull();
        assertThat(response.getOwner().getUserId()).isEqualTo("USER_01");
        assertThat(response.getOwner().getFullName()).isEqualTo("Van A Nguyen");

        assertThat(response.getBusinessDetails()).isNotNull();
        assertThat(response.getBusinessDetails().getBusinessName()).isEqualTo("Tech Shop Official");

        assertThat(response.getBankDetails()).isNotNull();
        assertThat(response.getBankDetails().getAccountHolderName()).isEqualTo("Nguyen Van A");

        assertThat(response.getPickupAddress()).isNotNull();
        assertThat(response.getPickupAddress().getCity()).isEqualTo("Ha Noi");

        assertThat(response.getReport()).isNotNull();
        assertThat(response.getReport().getTotalOrder()).isEqualTo(120);

        verify(sellerRepository).findBySellerId("SELLER_01");
        verify(sellerReportRepository).findBySellerId("SELLER_01");
        verify(productRepository).countBySellerId("SELLER_01");
    }

    @Test
    @DisplayName("Lấy chi tiết Seller KYC thành công khi chưa có report")
    void shouldReturnSellerDetailSuccessfully_WhenReportIsNull() {
        when(sellerRepository.findBySellerId("SELLER_01")).thenReturn(Optional.of(mockSeller));
        when(sellerReportRepository.findBySellerId("SELLER_01")).thenReturn(Optional.empty());
        when(productRepository.countBySellerId("SELLER_01")).thenReturn(0L);

        AdminSellerDetailResponseDto response = handler.handle(new GetAdminSellerDetailQuery("SELLER_01"));

        assertThat(response).isNotNull();
        assertThat(response.getSellerId()).isEqualTo("SELLER_01");
        assertThat(response.getTotalProducts()).isEqualTo(0L);
        assertThat(response.getReport()).isNotNull();
        assertThat(response.getReport().getTotalSales()).isEqualTo(0L);
    }

    @Test
    @DisplayName("Lấy chi tiết Seller thất bại: Ném ResourceNotFoundException khi Seller không tồn tại")
    void shouldThrowException_WhenSellerNotFound() {
        when(sellerRepository.findBySellerId("UNKNOWN")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> handler.handle(new GetAdminSellerDetailQuery("UNKNOWN")))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage(SellerConstant.SELLER_NOT_FOUND);
    }
}

