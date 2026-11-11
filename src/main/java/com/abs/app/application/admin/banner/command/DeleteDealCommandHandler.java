package com.abs.app.application.admin.banner.command;

import com.abs.app.common.constant.BannerConstant;
import com.abs.app.common.exception.ResourceNotFoundException;
import com.abs.app.domain.entity.Deal;
import com.abs.app.domain.repository.DealRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DeleteDealCommandHandler {

    private final DealRepository dealRepository;

    @Transactional
    public void handle(DeleteDealCommand command) {
        Deal deal = dealRepository.findById(command.getId())
                .orElseThrow(() -> new ResourceNotFoundException(BannerConstant.DEAL_NOT_FOUND));

        dealRepository.delete(deal);
    }
}

