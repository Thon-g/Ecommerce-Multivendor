package com.abs.app.application.order.event;

import com.abs.app.common.constant.OrderConstant;
import com.abs.app.common.exception.BusinessException;
import com.abs.app.domain.entity.Order;
import com.abs.app.domain.entity.Seller;
import com.abs.app.domain.entity.SellerReport;
import com.abs.app.domain.entity.Transaction;
import com.abs.app.domain.event.OrderDeliveredEvent;
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

    @EventListener
    @Transactional
    public void handleOrderDelivered(OrderDeliveredEvent event) {
        Order order = event.getOrder();
        log.info("Xử lý sự kiện giao hàng thành công cho đơn: {}", order.getOrderId());

        // 1. Tìm Seller entity từ sellerId lưu trong Order
        Seller seller = sellerRepository.findBySellerId(order.getSellerId())
                .orElseThrow(() -> new BusinessException(OrderConstant.USER_NOT_SELLER));

        // 2. Tạo Transaction ghi nhận dòng tiền Customer -> Seller
        Transaction transaction = new Transaction();
        transaction.setCustomer(order.getUser());
        transaction.setOrder(order);
        transaction.setSeller(seller);
        transactionRepository.save(transaction);

        // 3. Cập nhật SellerReport (tạo mới nếu chưa tồn tại)
        SellerReport report = sellerReportRepository.findBySellerId(order.getSellerId())
                .orElseGet(() -> {
                    SellerReport newReport = new SellerReport();
                    newReport.setSeller(seller);
                    return newReport;
                });

        Long orderAmount = Long.valueOf(order.getTotalSellingPrice());

        report.setTotalEarnings(report.getTotalEarnings() + orderAmount);
        report.setTotalSales(report.getTotalSales() + orderAmount);
        report.setTotalOrder(report.getTotalOrder() + 1);
        report.setTotalTransactions(report.getTotalTransactions() + 1);
        report.setNetEarnings(report.getTotalEarnings() - report.getTotalRefunds() - report.getTotalTax());

        sellerReportRepository.save(report);

        log.info("Đã tạo Transaction và cập nhật SellerReport cho Seller: {}", order.getSellerId());
    }
}
