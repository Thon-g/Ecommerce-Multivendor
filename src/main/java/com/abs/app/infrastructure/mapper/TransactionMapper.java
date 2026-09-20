package com.abs.app.infrastructure.mapper;

import com.abs.app.application.seller.transaction.dto.TransactionResponseDto;
import com.abs.app.domain.entity.Transaction;

public class TransactionMapper {

    public static TransactionResponseDto toTransactionDto(Transaction transaction) {
        if (transaction == null) return null;
        TransactionResponseDto dto = new TransactionResponseDto();
        dto.setId(transaction.getId());
        dto.setOrderId(transaction.getOrder().getOrderId());
        dto.setCustomerName(
                (transaction.getCustomer().getFirstName() != null ? transaction.getCustomer().getFirstName() : "")
                + " "
                + (transaction.getCustomer().getLastName() != null ? transaction.getCustomer().getLastName() : "")
        );
        dto.setOrderAmount(transaction.getOrder().getTotalSellingPrice());
        dto.setDate(transaction.getDate());
        return dto;
    }
}
