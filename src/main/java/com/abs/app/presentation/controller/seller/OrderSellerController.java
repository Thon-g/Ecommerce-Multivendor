package com.abs.app.presentation.controller.seller;

import com.abs.app.application.order.dto.OrderResponseDto;
import com.abs.app.application.order.query.GetSellerOrdersQuery;
import com.abs.app.application.order.query.GetSellerOrdersQueryHandler;
import com.abs.app.common.constant.OrderConstant;
import com.abs.app.common.response.ApiResponse;
import com.abs.app.infrastructure.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/seller/orders")
@RequiredArgsConstructor
public class OrderSellerController {

    private final GetSellerOrdersQueryHandler getSellerOrdersQueryHandler;

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
}
