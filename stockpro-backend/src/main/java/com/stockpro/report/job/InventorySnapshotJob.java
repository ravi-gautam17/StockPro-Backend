package com.stockpro.report.job;

import com.stockpro.product.domain.Product;
import com.stockpro.report.domain.InventorySnapshot;
import com.stockpro.report.repository.InventorySnapshotRepository;
import com.stockpro.warehouse.domain.StockLevel;
import com.stockpro.warehouse.repository.StockLevelRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Component
public class InventorySnapshotJob {

    private static final Logger log = LoggerFactory.getLogger(InventorySnapshotJob.class);

    private final StockLevelRepository stockLevelRepository;
    private final InventorySnapshotRepository snapshotRepository;

    public InventorySnapshotJob(StockLevelRepository stockLevelRepository,
                                InventorySnapshotRepository snapshotRepository) {
        this.stockLevelRepository = stockLevelRepository;
        this.snapshotRepository = snapshotRepository;
    }

    @Scheduled(cron = "0 0 0 * * *")
    @Transactional
    public void takeDailySnapshot() {
        LocalDate today = LocalDate.now();
        List<StockLevel> levels = stockLevelRepository.findAll();

        for (StockLevel sl : levels) {
            Product p = sl.getProduct();
            BigDecimal value = p.getCostPrice()
                    .multiply(BigDecimal.valueOf(sl.getQuantity()));

            InventorySnapshot snap = new InventorySnapshot();
            snap.setWarehouse(sl.getWarehouse());
            snap.setProduct(p);
            snap.setQuantity(sl.getQuantity());
            snap.setStockValue(value);
            snap.setSnapshotDate(today);

            snapshotRepository.save(snap);
        }

        log.info("Inventory snapshot completed for {} stock rows on {}", levels.size(), today);
    }
}