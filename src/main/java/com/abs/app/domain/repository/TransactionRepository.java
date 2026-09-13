package com.abs.app.domain.repository;

import com.abs.app.domain.entity.Transaction;

public interface TransactionRepository {
    Transaction save(Transaction transaction);
}
