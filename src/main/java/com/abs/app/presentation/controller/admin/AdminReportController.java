package com.abs.app.presentation.controller.admin;

import com.abs.app.application.admin.report.dto.AdminReportResponseDto;
import com.abs.app.application.admin.report.dto.AdminTransactionResponseDto;
import com.abs.app.application.admin.report.query.GetAdminReportQuery;
import com.abs.app.application.admin.report.query.GetAdminReportQueryHandler;
import com.abs.app.application.admin.report.query.GetAdminTransactionsQuery;
import com.abs.app.application.admin.report.query.GetAdminTransactionsQueryHandler;
import com.abs.app.common.constant.AdminReportConstant;
import com.abs.app.common.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/reports")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminReportController {

    private final GetAdminReportQueryHandler getAdminReportQueryHandler;
    private final GetAdminTransactionsQueryHandler getAdminTransactionsQueryHandler;

    @GetMapping("/dashboard")
    public ResponseEntity<ApiResponse<AdminReportResponseDto>> getDashboard() {
        GetAdminReportQuery query = new GetAdminReportQuery();
        AdminReportResponseDto response = getAdminReportQueryHandler.handle(query);

        return ResponseEntity.ok(new ApiResponse<>(
                true,
                AdminReportConstant.GET_DASHBOARD_SUCCESS,
                response
        ));
    }

    @GetMapping("/transactions")
    public ResponseEntity<ApiResponse<Page<AdminTransactionResponseDto>>> getTransactions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        GetAdminTransactionsQuery query = new GetAdminTransactionsQuery(
                PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "date"))
        );
        Page<AdminTransactionResponseDto> response = getAdminTransactionsQueryHandler.handle(query);

        return ResponseEntity.ok(new ApiResponse<>(
                true,
                AdminReportConstant.GET_TRANSACTIONS_SUCCESS,
                response
        ));
    }
}
