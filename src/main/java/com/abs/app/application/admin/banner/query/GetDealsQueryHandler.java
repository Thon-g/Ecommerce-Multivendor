package com.abs.app.application.admin.banner.query;

import com.abs.app.application.admin.banner.dto.DealResponseDto;
import com.abs.app.domain.entity.Deal;
import com.abs.app.domain.repository.DealRepository;
import com.abs.app.infrastructure.mapper.HomeBannerMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GetDealsQueryHandler {

    private final DealRepository dealRepository;

    @Transactional(readOnly = true)
    public List<DealResponseDto> handle(GetDealsQuery query) {
        List<Deal> list = dealRepository.findAll();
        return HomeBannerMapper.toDealResponseDtoList(list);
    }
}

