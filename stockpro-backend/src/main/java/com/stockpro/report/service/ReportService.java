package com.stockpro.report.service;

import com.stockpro.report.dto.InventorySnapshotRequestDTO;
import com.stockpro.report.dto.InventorySnapshotResponseDTO;
import com.stockpro.report.dto.ValuationResponseDTO;
import com.stockpro.report.mapper.InventorySnapshotMapper;
import com.stockpro.report.repository.InventorySnapshotRepository;
import com.stockpro.warehouse.repository.StockLevelRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class ReportService {

    private final StockLevelRepository stockLevelRepository;
    private final InventorySnapshotRepository snapshotRepository;

    public ReportService(StockLevelRepository stockLevelRepository,
                         InventorySnapshotRepository snapshotRepository) {
        this.stockLevelRepository = stockLevelRepository;
        this.snapshotRepository = snapshotRepository;
    }

    /** Total inventory valuation */
    public ValuationResponseDTO totalInventoryValuation() {
        BigDecimal v = stockLevelRepository.sumStockValuation();
        return InventorySnapshotMapper.toValuationDTO(
                v != null ? v : BigDecimal.ZERO
        );
    }

    /** Snapshot report */
    public List<InventorySnapshotResponseDTO> getSnapshots(InventorySnapshotRequestDTO request) {
        return snapshotRepository
                .findBySnapshotDateBetween(request.getFromDate(), request.getToDate())
                .stream()
                .map(InventorySnapshotMapper::toDTO)
                .toList();
    }
}