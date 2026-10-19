package com.abs.app.application.admin.report.query;

import com.abs.app.application.admin.report.dto.AdminReportResponseDto;
import com.abs.app.domain.entity.AdminReport;
import com.abs.app.domain.repository.AdminReportRepository;
import com.abs.app.domain.repository.SellerRepository;
import com.abs.app.domain.repository.UserRepository;
import com.abs.app.infrastructure.mapper.AdminReportMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GetAdminReportQueryHandler {

    private final AdminReportRepository adminReportRepository;
    private final UserRepository userRepository;
    private final SellerRepository sellerRepository;

    public AdminReportResponseDto handle(GetAdminReportQuery query) {
        AdminReport report = adminReportRepository.findFirst().orElse(null);
        long totalUsers = userRepository.count();
        long totalSellers = sellerRepository.count();

        return AdminReportMapper.toResponseDto(report, totalUsers, totalSellers);
    }
}
