package com.abs.app.infrastructure.mapper;

import com.abs.app.application.order.dto.OrderItemResponseDto;
import com.abs.app.application.order.dto.OrderResponseDto;
import com.abs.app.application.order.dto.PaymentOrderResponseDto;
import com.abs.app.application.publicapi.product.dto.SkuResponseDto;
import com.abs.app.domain.entity.Order;
import com.abs.app.domain.entity.OrderItem;
import com.abs.app.domain.entity.PaymentOrder;
import com.abs.app.domain.entity.ProductSku;

import java.util.stream.Collectors;

public class OrderMapper {

    public static PaymentOrderResponseDto toPaymentOrderResponseDto(PaymentOrder paymentOrder) {
        if (paymentOrder == null) return null;
        PaymentOrderResponseDto dto = new PaymentOrderResponseDto();
        dto.setId(paymentOrder.getId());
        dto.setAmount(paymentOrder.getAmount());
        dto.setStatus(paymentOrder.getStatus());
        dto.setPaymentMethod(paymentOrder.getPaymentMethod());
        if (paymentOrder.getOrders() != null) {
            dto.setOrders(paymentOrder.getOrders().stream()
                    .map(OrderMapper::toOrderResponseDto)
                    .collect(Collectors.toList()));
        }
        return dto;
    }

    public static OrderResponseDto toOrderResponseDto(Order order) {
        if (order == null) return null;
        OrderResponseDto dto = new OrderResponseDto();
        dto.setOrderId(order.getOrderId());
        dto.setSellerId(order.getSellerId());
        dto.setTotalMrpPrice(order.getTotalMrpPrice());
        dto.setTotalSellingPrice(order.getTotalSellingPrice());
        dto.setDiscount(order.getDiscount());
        dto.setOrderStatus(order.getOrderStatus());
        dto.setTotalItem(order.getTotalItem());
        dto.setPaymentStatus(order.getPaymentStatus());
        dto.setOrderDate(order.getOrderDate());
        dto.setDeliverDate(order.getDeliverDate());
        dto.setCancelReason(order.getCancelReason());

        if (order.getOrderItems() != null) {
            dto.setOrderItems(order.getOrderItems().stream()
                    .map(OrderMapper::toOrderItemResponseDto)
                    .collect(Collectors.toList()));
        }
        return dto;
    }

    public static OrderItemResponseDto toOrderItemResponseDto(OrderItem item) {
        if (item == null) return null;
        OrderItemResponseDto dto = new OrderItemResponseDto();
        dto.setId(item.getId());
        dto.setQuantity(item.getQuantity());
        dto.setMrpPrice(item.getMrpPrice());
        dto.setSellingPrice(item.getSellingPrice());

        if (item.getProduct() != null) {
            dto.setProduct(ProductMapper.toProductResponseDto(item.getProduct()));
        }

        if (item.getSku() != null) {
            ProductSku sku = item.getSku();
            dto.setSku(new SkuResponseDto(
                    sku.getId(),
                    sku.getSkuCode(),
                    sku.getColor(),
                    sku.getSize(),
                    sku.getQuantity(),
                    sku.getSellingPrice()
            ));
        }
        return dto;
    }
}
