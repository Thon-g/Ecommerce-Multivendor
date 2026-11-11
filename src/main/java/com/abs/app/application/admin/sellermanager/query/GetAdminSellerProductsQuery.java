package com.abs.app.application.admin.sellermanager.query;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GetAdminSellerProductsQuery {
    private String sellerId;
    private String keyword;
    private int page = 1;
    private int size = 10;
}

