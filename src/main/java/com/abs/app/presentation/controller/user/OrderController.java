package com.abs.app.presentation.controller.user;

import com.abs.app.application.order.command.CheckoutCommand;
import com.abs.app.application.order.command.CheckoutCommandHandler;
import com.abs.app.application.order.dto.CheckoutRequestDto;
import com.abs.app.application.order.dto.OrderResponseDto;
import com.abs.app.application.order.dto.PaymentOrderResponseDto;
import com.abs.app.application.order.query.GetUserOrdersQuery;
import com.abs.app.application.order.query.GetUserOrdersQueryHandler;
import com.abs.app.common.constant.OrderConstant;
import com.abs.app.common.response.ApiResponse;
import com.abs.app.infrastructure.security.SecurityUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final CheckoutCommandHandler checkoutCommandHandler;
    private final GetUserOrdersQueryHandler getUserOrdersQueryHandler;

    @PostMapping("/checkout")
    public ResponseEntity<ApiResponse<PaymentOrderResponseDto>> checkout(
            @Valid @RequestBody CheckoutRequestDto request) {
        String userId = SecurityUtils.getCurrentUserId();
        CheckoutCommand command = new CheckoutCommand(userId, request.getAddressId());
        PaymentOrderResponseDto response = checkoutCommandHandler.handle(command);

        return ResponseEntity.ok(new ApiResponse<>(
                true,
                OrderConstant.CHECKOUT_SUCCESS,
                response
        ));
    }

    @GetMapping("/user")
    public ResponseEntity<ApiResponse<List<OrderResponseDto>>> getUserOrders() {
        String userId = SecurityUtils.getCurrentUserId();
        GetUserOrdersQuery query = new GetUserOrdersQuery(userId);
        List<OrderResponseDto> response = getUserOrdersQueryHandler.handle(query);

        return ResponseEntity.ok(new ApiResponse<>(
                true,
                OrderConstant.GET_ORDERS_SUCCESS,
                response
        ));
    }
}
