package com.abs.app.application.seller.product.dto;

import com.abs.app.common.constant.ProductConstant;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import org.springframework.web.multipart.MultipartFile;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateProductRequestDto {
    @NotBlank(message = ProductConstant.TITLE_REQUIRED)
    private String title;

    @NotBlank(message = ProductConstant.DESCRIPTION_REQUIRED)
    private String description;

    @NotNull(message = ProductConstant.MRP_PRICE_REQUIRED)
    @Min(value = 0, message = ProductConstant.PRICE_MIN_INVALID)
    private Integer mrpPrice;

    @NotNull(message = ProductConstant.SELLING_PRICE_REQUIRED)
    @Min(value = 0, message = ProductConstant.PRICE_MIN_INVALID)
    private Integer sellingPrice;

    @NotNull(message = ProductConstant.QUANTITY_REQUIRED)
    @Min(value = 0, message = ProductConstant.QUANTITY_MIN_INVALID)
    private Integer quantity;

    private String color;

    private String sizes;

    private List<MultipartFile> images;

    @NotBlank(message = ProductConstant.CATEGORY_REQUIRED)
    private String categoryId;
}
