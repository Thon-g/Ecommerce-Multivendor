package com.abs.app.application.publicapi.product.dto;

import com.abs.app.application.publicapi.category.dto.CategoryResponseDto;
import com.abs.app.application.seller.profile.dto.SellerResponseDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProductResponseDto {
    private String id;
    private String title;
    private String description;
    private Integer mrpPrice;
    private Integer sellingPrice;
    private Integer discountPercent;
    private Integer quantity;
    private String color;
    private String sizes;
    private List<String> images;
    private Integer numRatings;
    private LocalDateTime createAt;
    
    private CategoryResponseDto category;
    private SellerResponseDto seller;
}
