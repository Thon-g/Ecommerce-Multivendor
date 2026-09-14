package com.abs.app.application.order.command;

import com.abs.app.common.exception.BusinessException;
import com.abs.app.domain.entity.Order;
import com.abs.app.domain.entity.Seller;
import com.abs.app.domain.entity.enums.OrderStatus;
import com.abs.app.domain.repository.OrderRepository;
import com.abs.app.domain.repository.SellerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UpdateOrderStatusCommandHandler {
    private final OrderRepository orderRepository;
    private final SellerRepository sellerRepository;

    @Transactional
    public void handle(UpdateOrderStatusCommand command) {
        Order order = orderRepository.findById(command.getOrderId())
                .orElseThrow(() -> new BusinessException(com.abs.app.common.constant.OrderConstant.ORDER_NOT_FOUND));

        Seller seller = sellerRepository.findByUserId(command.getUserId())
                .orElseThrow(() -> new BusinessException(com.abs.app.common.constant.OrderConstant.USER_NOT_SELLER));

        if (!order.getSellerId().equals(seller.getSellerId())) {
            throw new BusinessException(com.abs.app.common.constant.OrderConstant.ORDER_ACCESS_DENIED);
        }

        if (order.getOrderStatus() == OrderStatus.CANCELLED) {
            throw new BusinessException(com.abs.app.common.constant.OrderConstant.ORDER_UPDATE_CANCELLED);
        }

        order.setOrderStatus(command.getOrderStatus());
        orderRepository.save(order);
    }
}
