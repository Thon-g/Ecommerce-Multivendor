package com.abs.app.domain.repository;

import com.abs.app.domain.entity.AdminReport;
import java.util.Optional;

public interface AdminReportRepository {
    Optional<AdminReport> findFirst(); // Since there is only one system-wide admin report
    AdminReport save(AdminReport report);
}
