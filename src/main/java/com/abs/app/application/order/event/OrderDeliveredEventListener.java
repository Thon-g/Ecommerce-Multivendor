package com.abs.app.application.order.event;

import com.abs.app.common.constant.OrderConstant;
import com.abs.app.common.exception.BusinessException;
import com.abs.app.domain.entity.*;
import com.abs.app.domain.event.OrderDeliveredEvent;
import com.abs.app.domain.repository.AdminReportRepository;
import com.abs.app.domain.repository.SellerReportRepository;
import com.abs.app.domain.repository.SellerRepository;
import com.abs.app.domain.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderDeliveredEventListener {

    private final TransactionRepository transactionRepository;
    private final SellerReportRepository sellerReportRepository;
    private final SellerRepository sellerRepository;
    private final AdminReportRepository adminReportRepository;

    @EventListener
    @Transactional
    public void handleOrderDelivered(OrderDeliveredEvent event) {
        Order order = event.getOrder();
        log.info("Xử lý sự kiện giao hàng thành công cho đơn: {}", order.getOrderId());

        // 1. Tìm Seller entity từ sellerId lưu trong Order
        Seller seller = sellerRepository.findBySellerId(order.getSellerId())
                .orElseThrow(() -> new BusinessException(OrderConstant.USER_NOT_SELLER));

        // 2. Tính tổng platform fee từ các OrderItem
        int totalPlatformFee = 0;
        if (order.getOrderItems() != null) {
            for (OrderItem item : order.getOrderItems()) {
                if (item.getPlatformFee() != null) {
                    totalPlatformFee += item.getPlatformFee();
                }
            }
        }

        // 3. Tạo Transaction ghi nhận dòng tiền Customer -> Seller
        Transaction transaction = new Transaction();
        transaction.setCustomer(order.getUser());
        transaction.setOrder(order);
        transaction.setSeller(seller);
        transaction.setTotalPlatformFee(totalPlatformFee);
        transactionRepository.save(transaction);

        // 4. Cập nhật SellerReport (trừ đi phí sàn)
        SellerReport report = sellerReportRepository.findBySellerId(order.getSellerId())
                .orElseGet(() -> {
                    SellerReport newReport = new SellerReport();
                    newReport.setSeller(seller);
                    return newReport;
                });

        Long orderAmount = Long.valueOf(order.getTotalSellingPrice());
        Long sellerEarnings = orderAmount - totalPlatformFee;

        report.setTotalEarnings(report.getTotalEarnings() + sellerEarnings);
        report.setTotalSales(report.getTotalSales() + orderAmount); // Gross sales
        report.setTotalOrder(report.getTotalOrder() + 1);
        report.setTotalTransactions(report.getTotalTransactions() + 1);
        report.setNetEarnings(report.getTotalEarnings() - report.getTotalRefunds() - report.getTotalTax());

        sellerReportRepository.save(report);

        // 5. Cập nhật AdminReport
        AdminReport adminReport = adminReportRepository.findFirst().orElseGet(AdminReport::new);
        adminReport.setTotalEarnings(adminReport.getTotalEarnings() + totalPlatformFee);
        adminReport.setTotalSales(adminReport.getTotalSales() + orderAmount);
        adminReport.setTotalOrders(adminReport.getTotalOrders() + 1);
        adminReport.setTotalTransactions(adminReport.getTotalTransactions() + 1);
        adminReportRepository.save(adminReport);

        log.info("Đã tạo Transaction, thu phí sàn {} và cập nhật SellerReport cho Seller: {}", totalPlatformFee, order.getSellerId());
    }
}
