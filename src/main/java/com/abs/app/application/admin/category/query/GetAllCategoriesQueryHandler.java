package com.abs.app.application.admin.category.query;

import com.abs.app.application.admin.category.dto.CategoryResponseDto;
import com.abs.app.domain.entity.Category;
import com.abs.app.domain.repository.CategoryRepository;
import com.abs.app.infrastructure.mapper.CategoryMapper;
import lombok.RequiredArgsConstructor;
import com.abs.app.common.response.PageResponse;
import com.abs.app.common.util.PaginationUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GetAllCategoriesQueryHandler {

    private final CategoryRepository categoryRepository;

    @Transactional(readOnly = true)
    public PageResponse<CategoryResponseDto> handle(GetAllCategoriesQuery query) {
        Pageable pageable = PaginationUtil.createPageable(query.getPage(), query.getSize(), Sort.by("level").ascending());
        Page<Category> page = categoryRepository.search(query.getKeyword(), pageable);
        List<CategoryResponseDto> dtoList = CategoryMapper.toCategoryResponseDtoTree(page.getContent());

        return new PageResponse<>(
                dtoList,
                (int) page.getTotalElements(),
                query.getPage(),
                query.getSize()
        );
    }
}
