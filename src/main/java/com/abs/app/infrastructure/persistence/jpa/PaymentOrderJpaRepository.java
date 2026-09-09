package com.abs.app.infrastructure.persistence.jpa;

import com.abs.app.domain.entity.PaymentOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

public interface PaymentOrderJpaRepository extends JpaRepository<PaymentOrder, Long> {
}
