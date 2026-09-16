package com.abs.app.infrastructure.persistence.jpa;

import com.abs.app.domain.entity.AdminReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AdminReportJpaRepository extends JpaRepository<AdminReport, Long> {
}
