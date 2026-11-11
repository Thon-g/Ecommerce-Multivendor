package com.abs.app.infrastructure.persistence.adapter;

import com.abs.app.domain.entity.Deal;
import com.abs.app.domain.repository.DealRepository;
import com.abs.app.infrastructure.persistence.jpa.DealJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DealRepositoryImpl implements DealRepository {

    private final DealJpaRepository dealJpaRepository;

    @Override
    public Deal save(Deal deal) {
        return dealJpaRepository.save(deal);
    }

    @Override
    public Optional<Deal> findById(Long id) {
        return dealJpaRepository.findById(id);
    }

    @Override
    public List<Deal> findAll() {
        return dealJpaRepository.findAll();
    }

    @Override
    public void delete(Deal deal) {
        dealJpaRepository.delete(deal);
    }

    @Override
    public boolean existsByCategoryId(Long categoryId) {
        return dealJpaRepository.existsByCategoryId(categoryId);
    }
}

