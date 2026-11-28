package com.abs.app.application.chat.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductCardDto {
    private String productId;
    private String title;
    private String imageUrl;
    private Long mrpPrice;
    private Long sellingPrice;
    private Integer discountPercent;
    private Double rating;
    private String shopName;
    private String productUrl;
}
