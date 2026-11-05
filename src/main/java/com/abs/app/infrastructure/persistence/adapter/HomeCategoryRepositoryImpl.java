package com.abs.app.infrastructure.persistence.adapter;

import com.abs.app.domain.entity.HomeCategory;
import com.abs.app.domain.entity.enums.HomeCategorySection;
import com.abs.app.domain.repository.HomeCategoryRepository;
import com.abs.app.infrastructure.persistence.jpa.HomeCategoryJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class HomeCategoryRepositoryImpl implements HomeCategoryRepository {

    private final HomeCategoryJpaRepository homeCategoryJpaRepository;

    @Override
    public HomeCategory save(HomeCategory homeCategory) {
        return homeCategoryJpaRepository.save(homeCategory);
    }

    @Override
    public Optional<HomeCategory> findById(Long id) {
        return homeCategoryJpaRepository.findById(id);
    }

    @Override
    public List<HomeCategory> findBySection(HomeCategorySection section) {
        return homeCategoryJpaRepository.findBySection(section);
    }

    @Override
    public List<HomeCategory> findAll() {
        return homeCategoryJpaRepository.findAll();
    }

    @Override
    public void delete(HomeCategory homeCategory) {
        homeCategoryJpaRepository.delete(homeCategory);
    }

    @Override
    public boolean existsById(Long id) {
        return homeCategoryJpaRepository.existsById(id);
    }
}

