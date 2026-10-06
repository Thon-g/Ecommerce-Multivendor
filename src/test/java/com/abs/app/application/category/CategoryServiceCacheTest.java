package com.abs.app.application.category;

import com.abs.app.domain.entity.Category;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = CategoryServiceCacheTest.CacheTestConfig.class)
public class CategoryServiceCacheTest {

    @Configuration
    @EnableCaching
    static class CacheTestConfig {
        @Bean
        public CacheManager cacheManager() {
            return new ConcurrentMapCacheManager("commissionRate");
        }

        @Bean
        public CategoryService categoryService() {
            return new CategoryService();
        }
    }

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private CacheManager cacheManager;

    @Test
    void testGetCommissionRate_ShouldCacheResult_AndAvoidMultipleExecutions() {
        Category parent = new Category();
        parent.setId("cat-parent");
        parent.setCommissionRate(8.0);

        Category child = new Category();
        child.setId("cat-child");
        child.setParentCategory(parent);

        Double rate1 = categoryService.getCommissionRate(child);
        assertEquals(8.0, rate1);

        Double rate2 = categoryService.getCommissionRate(child);
        assertEquals(8.0, rate2);

        // Verify the cache actually contains the value
        assertNotNull(cacheManager.getCache("commissionRate").get("cat-child"));
        assertEquals(8.0, cacheManager.getCache("commissionRate").get("cat-child").get());
    }
}
