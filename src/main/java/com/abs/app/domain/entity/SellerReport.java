package com.abs.app.domain.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "seller_reports")
public class SellerReport {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "seller_report_id")
    private Long id;

    @OneToOne
    @JoinColumn(name = "seller_id", nullable = false, unique = true)
    private Seller seller;

    @Column(name = "total_earnings", nullable = false)
    private Long totalEarnings = 0L;

    @Column(name = "total_sales", nullable = false)
    private Long totalSales = 0L;

    @Column(name = "total_refunds", nullable = false)
    private Long totalRefunds = 0L;

    @Column(name = "total_tax", nullable = false)
    private Long totalTax = 0L;

    @Column(name = "net_earnings", nullable = false)
    private Long netEarnings = 0L;

    @Column(name = "total_order", nullable = false)
    private Integer totalOrder = 0;

    @Column(name = "canceled_orders", nullable = false)
    private Integer canceledOrders = 0;

    @Column(name = "total_transactions", nullable = false)
    private Integer totalTransactions = 0;
}
