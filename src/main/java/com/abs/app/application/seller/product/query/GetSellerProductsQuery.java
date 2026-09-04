package com.abs.app.application.seller.product.query;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GetSellerProductsQuery {
    private String keyword;
    private String categoryId;
    private String userId;
    private int page = 1;
    private int size = 10;
}
