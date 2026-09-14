package com.abs.app.application.seller.transaction.query;

import com.abs.app.application.seller.transaction.dto.TransactionResponseDto;
import com.abs.app.common.constant.OrderConstant;
import com.abs.app.common.exception.BusinessException;
import com.abs.app.domain.entity.Seller;
import com.abs.app.domain.repository.SellerRepository;
import com.abs.app.domain.repository.TransactionRepository;
import com.abs.app.infrastructure.mapper.TransactionMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GetSellerTransactionsQueryHandler {

    private final SellerRepository sellerRepository;
    private final TransactionRepository transactionRepository;

    public Page<TransactionResponseDto> handle(GetSellerTransactionsQuery query) {
        Seller seller = sellerRepository.findByUserId(query.getUserId())
                .orElseThrow(() -> new BusinessException(OrderConstant.USER_NOT_SELLER));

        return transactionRepository.findBySellerId(seller.getSellerId(), query.getPageable())
                .map(TransactionMapper::toTransactionDto);
    }
}
