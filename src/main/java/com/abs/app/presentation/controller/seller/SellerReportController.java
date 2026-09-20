package com.abs.app.presentation.controller.seller;

import com.abs.app.application.seller.report.dto.SellerReportResponseDto;
import com.abs.app.application.seller.transaction.dto.TransactionResponseDto;
import com.abs.app.application.seller.report.query.GetSellerReportQuery;
import com.abs.app.application.seller.report.query.GetSellerReportQueryHandler;
import com.abs.app.application.seller.transaction.query.GetSellerTransactionsQuery;
import com.abs.app.application.seller.transaction.query.GetSellerTransactionsQueryHandler;
import com.abs.app.common.constant.SellerReportConstant;
import com.abs.app.common.response.ApiResponse;
import com.abs.app.infrastructure.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/seller/reports")
@RequiredArgsConstructor
public class SellerReportController {

    private final GetSellerReportQueryHandler getSellerReportQueryHandler;
    private final GetSellerTransactionsQueryHandler getSellerTransactionsQueryHandler;

    @GetMapping("/dashboard")
    public ResponseEntity<ApiResponse<SellerReportResponseDto>> getDashboard() {
        String userId = SecurityUtils.getCurrentUserId();
        GetSellerReportQuery query = new GetSellerReportQuery(userId);
        SellerReportResponseDto response = getSellerReportQueryHandler.handle(query);

        return ResponseEntity.ok(new ApiResponse<>(
                true,
                SellerReportConstant.GET_DASHBOARD_SUCCESS,
                response
        ));
    }

    @GetMapping("/transactions")
    public ResponseEntity<ApiResponse<Page<TransactionResponseDto>>> getTransactions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        String userId = SecurityUtils.getCurrentUserId();
        GetSellerTransactionsQuery query = new GetSellerTransactionsQuery(
                userId,
                PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "date"))
        );
        Page<TransactionResponseDto> response = getSellerTransactionsQueryHandler.handle(query);

        return ResponseEntity.ok(new ApiResponse<>(
                true,
                SellerReportConstant.GET_TRANSACTIONS_SUCCESS,
                response
        ));
    }
}
