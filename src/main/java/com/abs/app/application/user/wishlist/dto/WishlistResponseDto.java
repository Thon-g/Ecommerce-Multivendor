package com.abs.app.application.user.wishlist.dto;

import com.abs.app.application.publicapi.product.dto.ProductResponseDto;
import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
public class WishlistResponseDto {
    private Long id;
    private String userId;
    private List<ProductResponseDto> products;
}
