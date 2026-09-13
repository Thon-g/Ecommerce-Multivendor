package com.abs.app.application.order.command;

import com.abs.app.application.order.dto.PaymentOrderResponseDto;
import com.abs.app.common.constant.CartConstant;
import com.abs.app.common.constant.OrderConstant;
import com.abs.app.common.exception.BusinessException;
import com.abs.app.common.exception.OutOfStockException;
import com.abs.app.domain.entity.*;
import com.abs.app.domain.entity.enums.OrderStatus;
import com.abs.app.domain.entity.enums.PaymentMethod;
import com.abs.app.domain.entity.enums.PaymentOrderStatus;
import com.abs.app.domain.entity.enums.PaymentStatus;
import com.abs.app.domain.repository.*;
import com.abs.app.infrastructure.mapper.OrderMapper;
import com.abs.app.common.util.GenerateIdUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CheckoutCommandHandler {

    private final CartRepository cartRepository;
    private final AddressRepository addressRepository;
    private final ProductSkuRepository productSkuRepository;
    private final OrderRepository orderRepository;
    private final PaymentOrderRepository paymentOrderRepository;

    @Transactional
    public PaymentOrderResponseDto handle(CheckoutCommand command) {
        // 1. Fetch Cart
        Cart cart = cartRepository.findByUserId(command.getUserId())
                .orElseThrow(() -> new BusinessException(CartConstant.CART_NOT_FOUND));

        if (cart.getCartItems() == null || cart.getCartItems().isEmpty()) {
            throw new BusinessException(OrderConstant.CART_EMPTY);
        }

        // 2. Fetch Address
        Address address = addressRepository.findById(command.getAddressId())
                .orElseThrow(() -> new BusinessException(OrderConstant.INVALID_ADDRESS));

        boolean belongsToUser = cart.getUser().getAddresses().stream()
                .anyMatch(a -> a.getId().equals(address.getId()));

        if (!belongsToUser) {
            throw new BusinessException(OrderConstant.INVALID_ADDRESS);
        }

        // 3. Group CartItems by Seller
        Map<String, List<CartItem>> itemsBySeller = cart.getCartItems().stream()
                .collect(Collectors.groupingBy(item -> item.getProduct().getSeller().getSellerId()));

        Set<Order> createdOrders = new HashSet<>();
        Long totalPaymentAmount = 0L;

        // 4. Process each seller's order
        for (Map.Entry<String, List<CartItem>> entry : itemsBySeller.entrySet()) {
            String sellerId = entry.getKey();
            List<CartItem> sellerItems = entry.getValue();

            Order order = new Order();
            order.setOrderId(GenerateIdUtil.GenerateId(OrderConstant.SALT, OrderConstant.LIMIT));
            order.setUser(cart.getUser());
            order.setSellerId(sellerId);
            order.setShippingAddress(address);
            order.setOrderStatus(OrderStatus.PLACED); // COD -> placed immediately
            order.setPaymentStatus(PaymentStatus.PENDING); // Not paid yet

            int totalSellingPrice = 0;
            double totalMrpPrice = 0;
            int totalItem = 0;

            // 5. Process items, check stock, apply pessimistic lock
            for (CartItem cartItem : sellerItems) {
                ProductSku sku = productSkuRepository.findByIdWithLock(cartItem.getSku().getId())
                        .orElseThrow(() -> new BusinessException("Không tìm thấy SKU: " + cartItem.getSku().getId()));

                if (sku.getQuantity() < cartItem.getQuantity()) {
                    throw new OutOfStockException(String.format(OrderConstant.OUT_OF_STOCK, cartItem.getProduct().getTitle()));
                }

                // Deduct stock
                sku.setQuantity(sku.getQuantity() - cartItem.getQuantity());
                productSkuRepository.save(sku);

                OrderItem orderItem = new OrderItem();
                orderItem.setOrder(order);
                orderItem.setProduct(cartItem.getProduct());
                orderItem.setSku(sku);
                orderItem.setQuantity(cartItem.getQuantity());
                orderItem.setMrpPrice(cartItem.getMrpPrice());
                orderItem.setSellingPrice(cartItem.getSellingPrice());
                orderItem.setUserId(command.getUserId());

                order.getOrderItems().add(orderItem);

                totalSellingPrice += (cartItem.getSellingPrice() * cartItem.getQuantity());
                totalMrpPrice += (cartItem.getMrpPrice() * cartItem.getQuantity());
                totalItem += cartItem.getQuantity();
            }

            order.setTotalSellingPrice(totalSellingPrice);
            order.setTotalMrpPrice(totalMrpPrice);
            order.setTotalItem(totalItem);
            order.setDiscount((int) (totalMrpPrice - totalSellingPrice));

            orderRepository.save(order);
            createdOrders.add(order);
            totalPaymentAmount += totalSellingPrice;
        }

        // 6. Create PaymentOrder
        PaymentOrder paymentOrder = new PaymentOrder();
        paymentOrder.setAmount(totalPaymentAmount);
        paymentOrder.setUser(cart.getUser());
        paymentOrder.setOrders(createdOrders);
        paymentOrder.setPaymentMethod(PaymentMethod.CASH);
        paymentOrder.setStatus(PaymentOrderStatus.SUCCESS); // COD is technically "success" in placing the order

        paymentOrder = paymentOrderRepository.save(paymentOrder);

        // 7. Clear Cart
        cart.getCartItems().clear();
        cart.setTotalItem(0);
        cart.setTotalMrpPrice(0);
        cart.setTotalSellingPrice(0.0);
        cart.setDiscount(0);
        cart.setCouponCode(null);
        cartRepository.save(cart);

        return OrderMapper.toPaymentOrderResponseDto(paymentOrder);
    }
}
