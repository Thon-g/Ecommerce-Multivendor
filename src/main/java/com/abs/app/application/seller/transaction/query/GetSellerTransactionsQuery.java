package com.abs.app.application.seller.transaction.query;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.domain.Pageable;

@Data
@AllArgsConstructor
public class GetSellerTransactionsQuery {
    private String userId;
    private Pageable pageable;
}
