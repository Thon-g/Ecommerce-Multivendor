package com.abs.app.application.order.command;

import com.abs.app.application.order.dto.PaymentOrderResponseDto;
import com.abs.app.common.exception.BusinessException;
import com.abs.app.common.exception.OutOfStockException;
import com.abs.app.domain.entity.*;
import com.abs.app.domain.entity.enums.PaymentOrderStatus;
import com.abs.app.domain.repository.*;
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
    void handle_Success_ShouldCreateOrderAndPaymentOrder() {
        // Arrange
        CheckoutCommand command = new CheckoutCommand("user-1", 1L);

        when(cartRepository.findByUserId("user-1")).thenReturn(Optional.of(cart));
        when(addressRepository.findById(1L)).thenReturn(Optional.of(address));
        when(productSkuRepository.deductStock(1L, 2)).thenReturn(1);
        when(paymentOrderRepository.save(any(PaymentOrder.class))).thenAnswer(i -> {
            PaymentOrder po = i.getArgument(0);
            po.setId(1L);
            return po;
        });

        // Act
        PaymentOrderResponseDto response = checkoutCommandHandler.handle(command);

        // Assert
        assertNotNull(response);
        assertEquals(PaymentOrderStatus.SUCCESS, response.getStatus());
        assertEquals(200L, response.getAmount());

        // Verify deductStock was called (native query doesn't modify in-memory object)
        verify(productSkuRepository, times(1)).deductStock(1L, 2);

        // Verify Cart cleared
        assertTrue(cart.getCartItems().isEmpty());
        assertEquals(0, cart.getTotalItem());

        // Verify platform fee calculation (2 items * 100 selling price = 200 total * 10% = 20)
        org.mockito.ArgumentCaptor<Order> orderCaptor = org.mockito.ArgumentCaptor.forClass(Order.class);
        verify(orderRepository, times(1)).save(orderCaptor.capture());
        Order savedOrder = orderCaptor.getValue();
        assertEquals(1, savedOrder.getOrderItems().size());
        assertEquals(20, savedOrder.getOrderItems().iterator().next().getPlatformFee());

        verify(paymentOrderRepository, times(1)).save(any(PaymentOrder.class));
        verify(cartRepository, times(1)).save(cart);
    }

    @Test
    void handle_OutOfStock_ShouldThrowException() {
        // Arrange
        cartItem.setQuantity(15); // Require 15 but sku only has 10
        CheckoutCommand command = new CheckoutCommand("user-1", 1L);

        when(cartRepository.findByUserId("user-1")).thenReturn(Optional.of(cart));
        when(addressRepository.findById(1L)).thenReturn(Optional.of(address));
        when(productSkuRepository.deductStock(1L, 15)).thenReturn(0);

        // Act & Assert
        assertThrows(OutOfStockException.class, () -> checkoutCommandHandler.handle(command));

        // Verify NO saves were made
        verify(orderRepository, never()).save(any(Order.class));
        verify(paymentOrderRepository, never()).save(any(PaymentOrder.class));
        assertEquals(10, sku.getQuantity()); // Stock remains untouched
    }

    @Test
    void handle_EmptyCart_ShouldThrowBusinessException() {
        // Arrange
        cart.getCartItems().clear();
        CheckoutCommand command = new CheckoutCommand("user-1", 1L);

        when(cartRepository.findByUserId("user-1")).thenReturn(Optional.of(cart));

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class, () -> checkoutCommandHandler.handle(command));
        assertEquals("Giỏ hàng của bạn đang trống, không thể thanh toán.", exception.getMessage());
    }
}
