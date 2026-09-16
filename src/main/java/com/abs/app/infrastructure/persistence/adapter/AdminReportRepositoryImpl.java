package com.abs.app.infrastructure.persistence.adapter;

import com.abs.app.domain.entity.AdminReport;
import com.abs.app.domain.repository.AdminReportRepository;
import com.abs.app.infrastructure.persistence.jpa.AdminReportJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class AdminReportRepositoryImpl implements AdminReportRepository {

    private final AdminReportJpaRepository adminReportJpaRepository;

    @Override
    public Optional<AdminReport> findFirst() {
        return adminReportJpaRepository.findAll().stream().findFirst();
    }

    @Override
    public AdminReport save(AdminReport report) {
        return adminReportJpaRepository.save(report);
    }
}
