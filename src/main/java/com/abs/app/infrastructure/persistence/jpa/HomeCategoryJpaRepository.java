package com.abs.app.infrastructure.persistence.jpa;

import com.abs.app.domain.entity.HomeCategory;
import com.abs.app.domain.entity.enums.HomeCategorySection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HomeCategoryJpaRepository extends JpaRepository<HomeCategory, Long> {
    List<HomeCategory> findBySection(HomeCategorySection section);
}

