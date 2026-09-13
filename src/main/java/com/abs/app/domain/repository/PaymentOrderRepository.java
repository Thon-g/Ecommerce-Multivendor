package com.abs.app.domain.repository;

import com.abs.app.domain.entity.PaymentOrder;
import java.util.Optional;

public interface PaymentOrderRepository {
    PaymentOrder save(PaymentOrder paymentOrder);
    Optional<PaymentOrder> findById(Long id);
}
