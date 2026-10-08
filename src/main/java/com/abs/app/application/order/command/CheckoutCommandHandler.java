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
import com.abs.app.domain.service.StockCacheService;
import com.abs.app.application.category.CategoryService;
import com.abs.app.infrastructure.mapper.OrderMapper;
import com.abs.app.common.util.GenerateIdUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CheckoutCommandHandler {

    private final CartRepository cartRepository;
    private final AddressRepository addressRepository;
    private final ProductSkuRepository productSkuRepository;
    private final OrderRepository orderRepository;
    private final PaymentOrderRepository paymentOrderRepository;
    private final CategoryService categoryService;
    private final StockCacheService stockCacheService;

    @Transactional
    public PaymentOrderResponseDto handle(CheckoutCommand command) {
        Cart cart = cartRepository.findByUserId(command.getUserId())
                .orElseThrow(() -> new BusinessException(CartConstant.CART_NOT_FOUND));

        if (cart.getCartItems() == null || cart.getCartItems().isEmpty()) {
            throw new BusinessException(OrderConstant.CART_EMPTY);
        }

        Address address = addressRepository.findById(command.getAddressId())
                .orElseThrow(() -> new BusinessException(OrderConstant.INVALID_ADDRESS));

        boolean belongsToUser = cart.getUser().getAddresses().stream()
                .anyMatch(a -> a.getId().equals(address.getId()));

        if (!belongsToUser) {
            throw new BusinessException(OrderConstant.INVALID_ADDRESS);
        }

        Map<String, List<CartItem>> itemsBySeller = cart.getCartItems().stream()
                .collect(Collectors.groupingBy(item -> item.getProduct().getSeller().getSellerId()));

        Set<Order> createdOrders = new HashSet<>();
        Long totalPaymentAmount = 0L;

        List<Map.Entry<Long, Integer>> redisDeductedSkus = new ArrayList<>();

        try {
            for (Map.Entry<String, List<CartItem>> entry : itemsBySeller.entrySet()) {
                String sellerId = entry.getKey();
                List<CartItem> sellerItems = entry.getValue();

                Order order = new Order();
                order.setOrderId(GenerateIdUtil.GenerateId(OrderConstant.SALT, OrderConstant.LIMIT));
                order.setUser(cart.getUser());
                order.setSellerId(sellerId);
                order.setShippingAddress(address);
                order.setOrderStatus(OrderStatus.PLACED);
                order.setPaymentStatus(PaymentStatus.PENDING);

                int totalSellingPrice = 0;
                double totalMrpPrice = 0;
                int totalItem = 0;

                for (CartItem cartItem : sellerItems) {
                    Long skuId = cartItem.getSku().getId();
                    int qty = cartItem.getQuantity();
                    int dbStock = cartItem.getSku().getQuantity();

                    long redisResult = stockCacheService.deductStock(skuId, qty, dbStock);

                    if (redisResult == 0) {
                        throw new OutOfStockException(
                                String.format(OrderConstant.OUT_OF_STOCK, cartItem.getProduct().getTitle()));
                    }

                    if (redisResult == 1) {
                        redisDeductedSkus.add(Map.entry(skuId, qty));
                    }

                    int updatedRows = productSkuRepository.deductStock(skuId, qty);
                    if (updatedRows == 0) {
                        throw new OutOfStockException(
                                String.format(OrderConstant.OUT_OF_STOCK, cartItem.getProduct().getTitle()));
                    }

                    ProductSku sku = cartItem.getSku();

                    OrderItem orderItem = new OrderItem();
                    orderItem.setOrder(order);
                    orderItem.setProduct(cartItem.getProduct());
                    orderItem.setSku(sku);
                    orderItem.setQuantity(qty);
                    orderItem.setMrpPrice(cartItem.getMrpPrice());
                    orderItem.setSellingPrice(cartItem.getSellingPrice());
                    orderItem.setUserId(command.getUserId());

                    Double rate = categoryService.getCommissionRate(cartItem.getProduct().getCategory());
                    int platformFee = (int) ((cartItem.getSellingPrice() * qty) * (rate / 100));
                    orderItem.setPlatformFee(platformFee);

                    order.getOrderItems().add(orderItem);

                    totalSellingPrice += (cartItem.getSellingPrice() * qty);
                    totalMrpPrice += (cartItem.getMrpPrice() * qty);
                    totalItem += qty;
                }

                order.setTotalSellingPrice(totalSellingPrice);
                order.setTotalMrpPrice(totalMrpPrice);
                order.setTotalItem(totalItem);
                order.setDiscount((int) (totalMrpPrice - totalSellingPrice));

                orderRepository.save(order);
                createdOrders.add(order);
                totalPaymentAmount += totalSellingPrice;
            }
        } catch (Exception e) {
            for (Map.Entry<Long, Integer> deducted : redisDeductedSkus) {
                stockCacheService.restoreStock(deducted.getKey(), deducted.getValue());
            }
            throw e;
        }

        PaymentOrder paymentOrder = new PaymentOrder();
        paymentOrder.setAmount(totalPaymentAmount);
        paymentOrder.setUser(cart.getUser());
        paymentOrder.setOrders(createdOrders);
        paymentOrder.setPaymentMethod(PaymentMethod.CASH);
        paymentOrder.setStatus(PaymentOrderStatus.SUCCESS);

        paymentOrder = paymentOrderRepository.save(paymentOrder);

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
