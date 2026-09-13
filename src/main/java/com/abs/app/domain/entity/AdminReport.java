package com.abs.app.domain.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "admin_reports")
public class AdminReport {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "admin_report_id")
    private Long id;

    @Column(name = "total_earnings", nullable = false)
    private Long totalEarnings = 0L;

    @Column(name = "total_sales", nullable = false)
    private Long totalSales = 0L;

    @Column(name = "total_refunds", nullable = false)
    private Long totalRefunds = 0L;

    @Column(name = "total_orders", nullable = false)
    private Integer totalOrders = 0;

    @Column(name = "canceled_orders", nullable = false)
    private Integer canceledOrders = 0;

    @Column(name = "total_transactions", nullable = false)
    private Integer totalTransactions = 0;

    @Column(name = "total_sellers", nullable = false)
    private Integer totalSellers = 0;

    @Column(name = "total_users", nullable = false)
    private Integer totalUsers = 0;
}
