package com.abs.app.application.admin.category.command;

import com.abs.app.common.constant.CategoryConstant;
import com.abs.app.common.exception.BusinessException;
import com.abs.app.common.exception.ResourceNotFoundException;
import com.abs.app.domain.entity.Category;
import com.abs.app.domain.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DeleteCategoryCommandHandler {

    private final CategoryRepository categoryRepository;

    @Transactional
    public void handle(DeleteCategoryCommand command) {
        Category category = categoryRepository.findById(command.getId())
                .orElseThrow(() -> new ResourceNotFoundException(CategoryConstant.CATEGORY_NOT_FOUND));

        if (categoryRepository.existsByParentCategoryId(category.getId())) {
            throw new BusinessException(CategoryConstant.CATEGORY_HAS_CHILDREN);
        }

        categoryRepository.delete(category);
    }
}
