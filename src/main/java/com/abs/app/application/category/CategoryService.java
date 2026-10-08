package com.abs.app.application.category;

import com.abs.app.domain.entity.Category;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class CategoryService {

    @Cacheable(value = "commissionRate", key = "#category.id", unless = "#category == null")
    public Double getCommissionRate(Category category) {
        if (category == null) {
            return 5.0; 
        }
        if (category.getCommissionRate() != null) {
            return category.getCommissionRate();
        }
        if (category.getParentCategory() != null) {
            return getCommissionRate(category.getParentCategory());
        }
        return 5.0;
    }
}
