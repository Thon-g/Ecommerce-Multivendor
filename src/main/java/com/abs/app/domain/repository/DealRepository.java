package com.abs.app.domain.repository;

import com.abs.app.domain.entity.Deal;

import java.util.List;
import java.util.Optional;

public interface DealRepository {
    Deal save(Deal deal);
    Optional<Deal> findById(Long id);
    List<Deal> findAll();
    void delete(Deal deal);
    boolean existsByCategoryId(Long categoryId);
}

