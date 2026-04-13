package com.stockpro.alert.job;

import com.stockpro.alert.service.AlertService;
import com.stockpro.purchase.domain.PurchaseOrderStatus;
import com.stockpro.purchase.repository.PurchaseOrderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

/**
 * Case study NFR: overdue receipt when expected delivery passed without full GRN.
 * Runs daily at 09:00 (adjust via {@code spring.task.scheduling} or server TZ).
 */
@Component
public class OverduePurchaseOrderAlertJob {

    private static final Logger log = LoggerFactory.getLogger(OverduePurchaseOrderAlertJob.class);

    private final PurchaseOrderRepository purchaseOrderRepository;
    private final AlertService alertService;

    public OverduePurchaseOrderAlertJob(PurchaseOrderRepository purchaseOrderRepository,
                                        AlertService alertService) {
        this.purchaseOrderRepository = purchaseOrderRepository;
        this.alertService = alertService;
    }

    @Scheduled(cron = "0 0 9 * * *")
    @Transactional
    public void run() {
        LocalDate today = LocalDate.now();
        var overdue = purchaseOrderRepository.findByStatusInAndExpectedDateBefore(
                List.of(PurchaseOrderStatus.APPROVED, PurchaseOrderStatus.PARTIALLY_RECEIVED), today);
        for (var po : overdue) {
            alertService.notifyOverdueReceipt(po.getId(), po.getReferenceNumber(), today);
        }
        if (!overdue.isEmpty()) {
            log.info("Overdue PO scan: {} open approved PO(s) past expected date", overdue.size());
        }
    }
}
