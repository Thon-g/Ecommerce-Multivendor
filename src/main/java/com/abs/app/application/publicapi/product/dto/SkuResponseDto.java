package com.abs.app.application.publicapi.product.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SkuResponseDto {
    private Long id;
    private String skuCode;
    private String color;
    private String size;
    private Integer quantity;
    private Integer sellingPrice;
}
