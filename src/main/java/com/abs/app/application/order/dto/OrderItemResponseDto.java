package com.abs.app.application.order.dto;

import com.abs.app.application.publicapi.product.dto.SkuResponseDto;
import lombok.Data;

@Data
public class OrderItemResponseDto {
    private Long id;
    private SkuResponseDto sku;
    private Integer quantity;
    private Integer mrpPrice;
    private Integer sellingPrice;
}
