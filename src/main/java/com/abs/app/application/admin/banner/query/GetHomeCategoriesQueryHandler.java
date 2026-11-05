package com.abs.app.application.admin.banner.query;

import com.abs.app.application.admin.banner.dto.HomeCategoryResponseDto;
import com.abs.app.domain.entity.HomeCategory;
import com.abs.app.domain.repository.HomeCategoryRepository;
import com.abs.app.infrastructure.mapper.HomeBannerMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GetHomeCategoriesQueryHandler {

    private final HomeCategoryRepository homeCategoryRepository;

    @Transactional(readOnly = true)
    public List<HomeCategoryResponseDto> handle(GetHomeCategoriesQuery query) {
        List<HomeCategory> list;
        if (query.getSection() != null) {
            list = homeCategoryRepository.findBySection(query.getSection());
        } else {
            list = homeCategoryRepository.findAll();
        }
        return HomeBannerMapper.toHomeCategoryResponseDtoList(list);
    }
}

