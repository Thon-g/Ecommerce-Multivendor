package com.abs.app.application.order.command;

import com.abs.app.common.constant.OrderConstant;
import com.abs.app.common.exception.BusinessException;
import com.abs.app.domain.entity.Order;
import com.abs.app.domain.entity.Seller;
import com.abs.app.domain.entity.enums.OrderStatus;
import com.abs.app.domain.entity.enums.PaymentStatus;
import com.abs.app.domain.event.OrderDeliveredEvent;
import com.abs.app.domain.repository.OrderRepository;
import com.abs.app.domain.repository.SellerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UpdateOrderStatusCommandHandler {
    private final OrderRepository orderRepository;
    private final SellerRepository sellerRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public void handle(UpdateOrderStatusCommand command) {
        Order order = orderRepository.findById(command.getOrderId())
                .orElseThrow(() -> new BusinessException(OrderConstant.ORDER_NOT_FOUND));

        Seller seller = sellerRepository.findByUserId(command.getUserId())
                .orElseThrow(() -> new BusinessException(OrderConstant.USER_NOT_SELLER));

        if (!order.getSellerId().equals(seller.getSellerId())) {
            throw new BusinessException(OrderConstant.ORDER_ACCESS_DENIED);
        }

        if (order.getOrderStatus() == OrderStatus.CANCELLED) {
            throw new BusinessException(OrderConstant.ORDER_UPDATE_CANCELLED);
        }

        order.setOrderStatus(command.getOrderStatus());

        // Khi đơn hàng COD giao thành công -> cập nhật PaymentStatus
        if (command.getOrderStatus() == OrderStatus.DELIVERED) {
            order.setPaymentStatus(PaymentStatus.COMPLETED);
        }

        orderRepository.save(order);

        // Bắn event sau khi lưu thành công để Listener tạo Transaction + cập nhật SellerReport
        if (command.getOrderStatus() == OrderStatus.DELIVERED) {
            eventPublisher.publishEvent(new OrderDeliveredEvent(this, order));
        }
    }
}
