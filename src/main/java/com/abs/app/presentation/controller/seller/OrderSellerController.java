package com.abs.app.presentation.controller.seller;

import com.abs.app.application.order.command.CancelOrderCommand;
import com.abs.app.application.order.command.CancelOrderCommandHandler;
import com.abs.app.application.order.command.UpdateOrderStatusCommand;
import com.abs.app.application.order.command.UpdateOrderStatusCommandHandler;
import com.abs.app.application.order.dto.CancelOrderRequestDto;
import com.abs.app.application.order.dto.OrderResponseDto;
import com.abs.app.application.order.dto.UpdateOrderStatusRequestDto;
import com.abs.app.application.order.query.GetSellerOrdersQuery;
import com.abs.app.application.order.query.GetSellerOrdersQueryHandler;
import com.abs.app.common.constant.OrderConstant;
import com.abs.app.common.response.ApiResponse;
import com.abs.app.infrastructure.security.SecurityUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/seller/orders")
@RequiredArgsConstructor
public class OrderSellerController {

    private final GetSellerOrdersQueryHandler getSellerOrdersQueryHandler;
    private final UpdateOrderStatusCommandHandler updateOrderStatusCommandHandler;
    private final CancelOrderCommandHandler cancelOrderCommandHandler;

    @GetMapping
    public ResponseEntity<ApiResponse<List<OrderResponseDto>>> getSellerOrders() {
        String userId = SecurityUtils.getCurrentUserId();
        
        GetSellerOrdersQuery query = new GetSellerOrdersQuery(userId);
        List<OrderResponseDto> response = getSellerOrdersQueryHandler.handle(query);

        return ResponseEntity.ok(new ApiResponse<>(
                true,
                OrderConstant.GET_SELLER_ORDERS_SUCCESS,
                response
        ));
    }
    @PatchMapping("/{orderId}/status")
    public ResponseEntity<ApiResponse<Void>> updateOrderStatus(
            @PathVariable String orderId,
            @Valid @RequestBody UpdateOrderStatusRequestDto dto) {
        String userId = SecurityUtils.getCurrentUserId();
        
        UpdateOrderStatusCommand command = new UpdateOrderStatusCommand(orderId, userId, dto.getOrderStatus());
        updateOrderStatusCommandHandler.handle(command);
        
        return ResponseEntity.ok(new ApiResponse<>(
                true,
                OrderConstant.UPDATE_ORDER_STATUS_SUCCESS,
                null
        ));
    }

    @PatchMapping("/{orderId}/cancel")
    public ResponseEntity<ApiResponse<Void>> cancelOrder(
            @PathVariable String orderId,
            @Valid @RequestBody CancelOrderRequestDto dto) {
        String userId = SecurityUtils.getCurrentUserId();
        
        CancelOrderCommand command = new CancelOrderCommand(orderId, userId, dto.getCancelReason());
        cancelOrderCommandHandler.handle(command);
        
        return ResponseEntity.ok(new ApiResponse<>(
                true,
                OrderConstant.CANCEL_ORDER_SUCCESS,
                null
        ));
    }
}
