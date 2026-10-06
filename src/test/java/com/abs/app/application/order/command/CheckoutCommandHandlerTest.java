package com.abs.app.application.order.command;

import com.abs.app.application.order.dto.PaymentOrderResponseDto;
import com.abs.app.common.exception.BusinessException;
import com.abs.app.common.exception.OutOfStockException;
import com.abs.app.application.category.CategoryService;
import com.abs.app.domain.entity.*;
import com.abs.app.domain.entity.enums.PaymentOrderStatus;
import com.abs.app.domain.repository.*;
import com.abs.app.domain.service.StockCacheService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CheckoutCommandHandlerTest {

    @Mock
    private CartRepository cartRepository;
    @Mock
    private AddressRepository addressRepository;
    @Mock
    private ProductSkuRepository productSkuRepository;
    @Mock
    private OrderRepository orderRepository;
    @Mock
    private PaymentOrderRepository paymentOrderRepository;
    @Mock
    private CategoryService categoryService;
    @Mock
    private StockCacheService stockCacheService;

    @InjectMocks
    private CheckoutCommandHandler checkoutCommandHandler;

    private User user;
    private Cart cart;
    private Address address;
    private ProductSku sku;
    private CartItem cartItem;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setUserId("user-1");

        address = new Address();
        address.setId(1L);
        user.setAddresses(new HashSet<>(Collections.singletonList(address)));

        Seller seller = new Seller();
        seller.setSellerId("seller-1");

        Category category = new Category();
        category.setId("cat-1");
        category.setCommissionRate(10.0);

        Product product = new Product();
        product.setId("product-1");
        product.setSeller(seller);
        product.setCategory(category);

        sku = new ProductSku();
        sku.setId(1L);
        sku.setQuantity(10);
        sku.setProduct(product);

        cartItem = new CartItem();
        cartItem.setProduct(product);
        cartItem.setSku(sku);
        cartItem.setQuantity(2);
        cartItem.setSellingPrice(100);
        cartItem.setMrpPrice(120);

        cart = new Cart();
        cart.setUser(user);
        cart.setCartItems(new HashSet<>(Collections.singletonList(cartItem)));
    }

    @Test
    void handle_Success_RedisDeductsStock_ShouldCreateOrderAndPaymentOrder() {
        CheckoutCommand command = new CheckoutCommand("user-1", 1L);

        when(cartRepository.findByUserId("user-1")).thenReturn(Optional.of(cart));
        when(addressRepository.findById(1L)).thenReturn(Optional.of(address));
        when(stockCacheService.deductStock(1L, 2, 10)).thenReturn(1L);
        when(productSkuRepository.deductStock(1L, 2)).thenReturn(1);
        when(categoryService.getCommissionRate(any())).thenReturn(10.0);
        when(paymentOrderRepository.save(any(PaymentOrder.class))).thenAnswer(i -> {
            PaymentOrder po = i.getArgument(0);
            po.setId(1L);
            return po;
        });

        PaymentOrderResponseDto response = checkoutCommandHandler.handle(command);

        assertNotNull(response);
        assertEquals(PaymentOrderStatus.SUCCESS, response.getStatus());
        assertEquals(200L, response.getAmount());

        verify(stockCacheService, times(1)).deductStock(1L, 2, 10);
        verify(productSkuRepository, times(1)).deductStock(1L, 2);

        assertTrue(cart.getCartItems().isEmpty());
        assertEquals(0, cart.getTotalItem());

        org.mockito.ArgumentCaptor<Order> orderCaptor = org.mockito.ArgumentCaptor.forClass(Order.class);
        verify(orderRepository, times(1)).save(orderCaptor.capture());
        Order savedOrder = orderCaptor.getValue();
        assertEquals(1, savedOrder.getOrderItems().size());
        assertEquals(20, savedOrder.getOrderItems().iterator().next().getPlatformFee());

        verify(stockCacheService, never()).restoreStock(anyLong(), anyInt());
    }

    @Test
    void handle_RedisOutOfStock_ShouldThrowException_WithoutTouchingDB() {
        CheckoutCommand command = new CheckoutCommand("user-1", 1L);

        when(cartRepository.findByUserId("user-1")).thenReturn(Optional.of(cart));
        when(addressRepository.findById(1L)).thenReturn(Optional.of(address));
        when(stockCacheService.deductStock(1L, 2, 10)).thenReturn(0L);

        assertThrows(OutOfStockException.class, () -> checkoutCommandHandler.handle(command));

        verify(productSkuRepository, never()).deductStock(anyLong(), anyInt());
        verify(orderRepository, never()).save(any(Order.class));
        verify(paymentOrderRepository, never()).save(any(PaymentOrder.class));
    }

    @Test
    void handle_RedisFallback_ShouldUseDBDirectly() {
        CheckoutCommand command = new CheckoutCommand("user-1", 1L);

        when(cartRepository.findByUserId("user-1")).thenReturn(Optional.of(cart));
        when(addressRepository.findById(1L)).thenReturn(Optional.of(address));
        when(stockCacheService.deductStock(1L, 2, 10)).thenReturn(-1L);
        when(productSkuRepository.deductStock(1L, 2)).thenReturn(1);
        when(categoryService.getCommissionRate(any())).thenReturn(10.0);
        when(paymentOrderRepository.save(any(PaymentOrder.class))).thenAnswer(i -> {
            PaymentOrder po = i.getArgument(0);
            po.setId(1L);
            return po;
        });

        PaymentOrderResponseDto response = checkoutCommandHandler.handle(command);

        assertNotNull(response);
        assertEquals(PaymentOrderStatus.SUCCESS, response.getStatus());

        verify(stockCacheService, never()).restoreStock(anyLong(), anyInt());
    }

    @Test
    void handle_DBFails_ShouldCompensateRedis() {
        CheckoutCommand command = new CheckoutCommand("user-1", 1L);

        when(cartRepository.findByUserId("user-1")).thenReturn(Optional.of(cart));
        when(addressRepository.findById(1L)).thenReturn(Optional.of(address));
        when(stockCacheService.deductStock(1L, 2, 10)).thenReturn(1L);
        when(productSkuRepository.deductStock(1L, 2)).thenReturn(0);

        assertThrows(OutOfStockException.class, () -> checkoutCommandHandler.handle(command));

        verify(stockCacheService, times(1)).restoreStock(1L, 2);
    }

    @Test
    void handle_EmptyCart_ShouldThrowBusinessException() {
        cart.getCartItems().clear();
        CheckoutCommand command = new CheckoutCommand("user-1", 1L);

        when(cartRepository.findByUserId("user-1")).thenReturn(Optional.of(cart));

        BusinessException exception = assertThrows(BusinessException.class, () -> checkoutCommandHandler.handle(command));
        assertEquals("Giỏ hàng của bạn đang trống, không thể thanh toán.", exception.getMessage());
    }
}

