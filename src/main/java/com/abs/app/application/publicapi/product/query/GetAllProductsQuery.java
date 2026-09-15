package com.abs.app.application.publicapi.product.query;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GetAllProductsQuery {
    private String keyword;
    private String categoryId;
    private int page = 1;
    private int size = 10;
}
