package com.abs.app.application.admin.report.query;

import com.abs.app.application.admin.report.dto.AdminTransactionResponseDto;
import com.abs.app.domain.repository.TransactionRepository;
import com.abs.app.infrastructure.mapper.AdminReportMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GetAdminTransactionsQueryHandler {

    private final TransactionRepository transactionRepository;

    public Page<AdminTransactionResponseDto> handle(GetAdminTransactionsQuery query) {
        return transactionRepository.findAll(query.getPageable())
                .map(AdminReportMapper::toTransactionDto);
    }
}
