package com.abs.app.domain.repository;

import com.abs.app.domain.entity.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TransactionRepository {
    Transaction save(Transaction transaction);
    Page<Transaction> findBySellerId(String sellerId, Pageable pageable);
}
