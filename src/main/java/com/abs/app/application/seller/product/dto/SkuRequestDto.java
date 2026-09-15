package com.abs.app.application.seller.product.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SkuRequestDto {
    private String skuCode;
    private String color;
    private String size;
    private Integer quantity;
    private Integer sellingPrice;
}
