package com.abs.app.application.order.command;

import com.abs.app.common.exception.BusinessException;
import com.abs.app.domain.entity.Order;
import com.abs.app.domain.entity.OrderItem;
import com.abs.app.domain.entity.ProductSku;
import com.abs.app.domain.entity.Seller;
import com.abs.app.domain.entity.enums.OrderStatus;
import com.abs.app.domain.repository.OrderRepository;
import com.abs.app.domain.repository.ProductSkuRepository;
import com.abs.app.domain.repository.SellerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CancelOrderCommandHandler {
    private final OrderRepository orderRepository;
    private final ProductSkuRepository productSkuRepository;
    private final SellerRepository sellerRepository;

    @Transactional
    public void handle(CancelOrderCommand command) {
        Order order = orderRepository.findById(command.getOrderId())
                .orElseThrow(() -> new BusinessException(com.abs.app.common.constant.OrderConstant.ORDER_NOT_FOUND));

        Seller seller = sellerRepository.findByUserId(command.getUserId())
                .orElseThrow(() -> new BusinessException(com.abs.app.common.constant.OrderConstant.USER_NOT_SELLER));

        if (!order.getSellerId().equals(seller.getSellerId())) {
            throw new BusinessException(com.abs.app.common.constant.OrderConstant.ORDER_ACCESS_DENIED);
        }

        if (order.getOrderStatus() == OrderStatus.DELIVERED || order.getOrderStatus() == OrderStatus.SHIPPED) {
            throw new BusinessException(com.abs.app.common.constant.OrderConstant.ORDER_CANCEL_SHIPPED);
        }

        if (order.getOrderStatus() == OrderStatus.CANCELLED) {
            throw new BusinessException(com.abs.app.common.constant.OrderConstant.ORDER_ALREADY_CANCELLED);
        }

        order.setOrderStatus(OrderStatus.CANCELLED);
        order.setCancelReason(command.getCancelReason());
        
        // Restore stock
        for (OrderItem item : order.getOrderItems()) {
            ProductSku sku = productSkuRepository.findByIdWithLock(item.getSku().getId())
                    .orElseThrow(() -> new BusinessException(com.abs.app.common.constant.OrderConstant.SKU_NOT_FOUND_FOR_ORDER));
            sku.setQuantity(sku.getQuantity() + item.getQuantity());
            productSkuRepository.save(sku);
        }

        orderRepository.save(order);
    }
}
