package com.abs.app.infrastructure.persistence.jpa;

import com.abs.app.domain.entity.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionJpaRepository extends JpaRepository<Transaction, Long> {
    Page<Transaction> findBySeller_SellerId(String sellerId, Pageable pageable);
}
