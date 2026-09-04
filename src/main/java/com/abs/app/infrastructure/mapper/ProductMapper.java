package com.abs.app.infrastructure.mapper;

import com.abs.app.application.publicapi.product.dto.ProductResponseDto;
import com.abs.app.domain.entity.Product;
import com.abs.app.domain.entity.ProductImage;

import java.util.List;
import java.util.stream.Collectors;

public class ProductMapper {
    private ProductMapper() {}

    public static ProductResponseDto toProductResponseDto(Product product) {
        if (product == null) {
            return null;
        }

        ProductResponseDto dto = new ProductResponseDto();
        dto.setId(product.getId());
        dto.setTitle(product.getTitle());
        dto.setDescription(product.getDescription());
        dto.setMrpPrice(product.getMrpPrice());
        dto.setSellingPrice(product.getSellingPrice());
        dto.setDiscountPercent(product.getDiscountPercent());
        dto.setQuantity(product.getQuantity());
        dto.setColor(product.getColor());
        dto.setSizes(product.getSizes());
        
        if (product.getImages() != null) {
            List<String> imageUrls = product.getImages().stream()
                    .sorted((img1, img2) -> Boolean.compare(img2.getIsMainImage(), img1.getIsMainImage()))
                    .map(ProductImage::getImageUrl)
                    .collect(Collectors.toList());
            dto.setImages(imageUrls);
        }

        dto.setNumRatings(product.getNumRatings());
        dto.setCreateAt(product.getCreateAt());

        if (product.getCategory() != null) {
            dto.setCategory(CategoryMapper.toCategoryResponseDto(product.getCategory()));
        }

        if (product.getSeller() != null) {
            dto.setSeller(SellerMapper.toSellerResponseDto(product.getSeller()));
        }

        return dto;
    }
    
    public static List<ProductResponseDto> toProductResponseDtoList(List<Product> products) {
        if (products == null) return null;
        return products.stream()
                .map(ProductMapper::toProductResponseDto)
                .collect(Collectors.toList());
    }
}
