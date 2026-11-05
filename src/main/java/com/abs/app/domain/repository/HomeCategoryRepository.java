package com.abs.app.domain.repository;

import com.abs.app.domain.entity.HomeCategory;
import com.abs.app.domain.entity.enums.HomeCategorySection;

import java.util.List;
import java.util.Optional;

public interface HomeCategoryRepository {
    HomeCategory save(HomeCategory homeCategory);
    Optional<HomeCategory> findById(Long id);
    List<HomeCategory> findBySection(HomeCategorySection section);
    List<HomeCategory> findAll();
    void delete(HomeCategory homeCategory);
    boolean existsById(Long id);
}

