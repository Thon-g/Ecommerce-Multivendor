package com.abs.app.infrastructure.persistence.jpa;

import com.abs.app.domain.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

public interface TransactionJpaRepository extends JpaRepository<Transaction, Long> {
}
