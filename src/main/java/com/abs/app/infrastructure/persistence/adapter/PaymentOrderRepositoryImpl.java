package com.abs.app.infrastructure.persistence.adapter;

import com.abs.app.domain.entity.PaymentOrder;
import com.abs.app.domain.repository.PaymentOrderRepository;
import com.abs.app.infrastructure.persistence.jpa.PaymentOrderJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PaymentOrderRepositoryImpl implements PaymentOrderRepository {

    private final PaymentOrderJpaRepository jpaRepository;

    @Override
    public PaymentOrder save(PaymentOrder paymentOrder) {
        return jpaRepository.save(paymentOrder);
    }

    @Override
    public Optional<PaymentOrder> findById(Long id) {
        return jpaRepository.findById(id);
    }
}
