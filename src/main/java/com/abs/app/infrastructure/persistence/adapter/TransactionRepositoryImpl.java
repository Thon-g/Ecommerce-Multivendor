package com.abs.app.infrastructure.persistence.adapter;

import com.abs.app.domain.entity.Transaction;
import com.abs.app.domain.repository.TransactionRepository;
import com.abs.app.infrastructure.persistence.jpa.TransactionJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TransactionRepositoryImpl implements TransactionRepository {

    private final TransactionJpaRepository jpaRepository;

    @Override
    public Transaction save(Transaction transaction) {
        return jpaRepository.save(transaction);
    }

    @Override
    public Page<Transaction> findBySellerId(String sellerId, Pageable pageable) {
        return jpaRepository.findBySeller_SellerId(sellerId, pageable);
    }
}
